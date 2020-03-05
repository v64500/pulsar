package ai.platon.pulsar.proxy

import ai.platon.pulsar.common.*
import ai.platon.pulsar.common.config.AppConstants.INTERNAL_PROXY_SERVER_PORT_BASE
import ai.platon.pulsar.common.config.CapabilityTypes.PROXY_SERVER_BOSS_THREADS
import ai.platon.pulsar.common.config.CapabilityTypes.PROXY_SERVER_WORKER_THREADS
import ai.platon.pulsar.common.config.ImmutableConfig
import ai.platon.pulsar.common.proxy.ProxyEntry
import ai.platon.pulsar.proxy.server.*
import io.netty.channel.Channel
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import io.netty.util.ResourceLeakDetector
import org.slf4j.LoggerFactory
import org.springframework.util.SocketUtils
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private class ForwardServer(val proxyServerConfig: HttpProxyServerConfig): AutoCloseable {

    private val log = LoggerFactory.getLogger(ProxyConnector::class.java)
    private val proxyLog = HttpProxyServer.log
    private var server: HttpProxyServer? = null

    private val proxyInterceptInitializer = object: HttpProxyInterceptInitializer() {

        override fun init(pipeline: HttpProxyInterceptPipeline) {
            pipeline.addLast(object : FullRequestIntercept() {
                override fun match(httpRequest: HttpRequest, pipeline: HttpProxyInterceptPipeline): Boolean {
                    return log.isTraceEnabled
                }

                override fun handelRequest(httpRequest: FullHttpRequest, pipeline: HttpProxyInterceptPipeline) {
                    val message = String.format("Ready to download %s", httpRequest.headers())
                    proxyLog.write(SimpleLogger.DEBUG, "[proxy]", message)
                }
            })

            pipeline.addLast(object : FullResponseIntercept() {
                override fun match(httpRequest: HttpRequest, httpResponse: HttpResponse, pipeline: HttpProxyInterceptPipeline): Boolean {
                    return log.isTraceEnabled
                }

                override fun handelResponse(httpRequest: HttpRequest, httpResponse: FullHttpResponse, pipeline: HttpProxyInterceptPipeline) {
                    val message = String.format("Got resource %s, %s", httpResponse.status(), httpResponse.headers())
                    proxyLog.write(SimpleLogger.DEBUG, "[proxy]", message)
                }
            })
        }
    }

    val httpProxyExceptionHandle = object: HttpProxyExceptionHandle() {
        private val proxyLog = HttpProxyServer.log

        override fun beforeCatch(clientChannel: Channel, cause: Throwable) {
            // log.warn("Internal proxy error - {}", StringUtil.stringifyException(cause))
        }

        override fun afterCatch(clientChannel: Channel, proxyChannel: Channel, cause: Throwable) {
            var message = cause.message
            when (cause) {
                is io.netty.handler.proxy.ProxyConnectException -> {
                    // TODO: handle io.netty.handler.proxy.ProxyConnectException: http, none, /117.69.129.113:4248 => img59.ddimg.cn:80, disconnected
                    message = StringUtil.simplifyException(cause)
                }
            }

            if (message == null) {
                log.warn(StringUtil.stringifyException(cause))
                return
            }

            // log.warn(StringUtil.simplifyException(cause))
            proxyLog.write(SimpleLogger.WARN, javaClass, message)
        }
    }

    fun start(port: Int) {
        // ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED)
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE)
        server = HttpProxyServer(proxyServerConfig, proxyInterceptInitializer, httpProxyExceptionHandle)
        server?.start(port)
    }

    override fun close() {
        server?.use { it.close() }
    }
}

class ProxyConnector(
        private val metricsSystem: MetricsSystem,
        private val conf: ImmutableConfig
): AutoCloseable {

    companion object {
        const val TEST_SCRIPT = "wget https://www.baidu.com/ -e use_proxy=yes -e http_proxy=127.0.0.1:{port}"
    }

    private val log = LoggerFactory.getLogger(ProxyConnector::class.java)

    private val numBossGroupThreads = conf.getInt(PROXY_SERVER_BOSS_THREADS, 1)
    private val numWorkerGroupThreads = conf.getInt(PROXY_SERVER_WORKER_THREADS, 2)
    private val numProxyGroupThreads = conf.getInt(PROXY_SERVER_WORKER_THREADS, 2)
    private var forwardServer: ForwardServer? = null
    private var forwardServerThread: Thread? = null
    private val threadJoinTimeout = Duration.ofSeconds(30)

    private val pollingInterval = Duration.ofMillis(100)
    private val proxyTimeout = Duration.ofMinutes(3)

    private val closed = AtomicBoolean()
    private val lock: Lock = ReentrantLock()
    private val connected: Condition = lock.newCondition()
    private val disconnected: Condition = lock.newCondition()

    var numTotalConnects = 0
    var port = -1
    val proxyEntry = AtomicReference<ProxyEntry>()
    val isOnline get() = proxyEntry.get() != null
    val isClosed get() = closed.get()

    fun ensureOnline(): Boolean {
        if (isClosed) {
            return false
        }

        if (!isOnline) {
            log.info("No proxy online, waiting ...")
        }

        lock.withLock {
            var i = 0
            val maxRound = proxyTimeout.toMillis() / pollingInterval.toMillis()
            while (!isClosed && !isOnline && ++i < maxRound && !Thread.currentThread().isInterrupted) {
                connected.await(pollingInterval.toMillis(), TimeUnit.MILLISECONDS)
            }
        }

        return !isClosed && isOnline
    }

    /**
     * Connect to a new proxy, disconnect last proxy if exists.
     * */
    @Synchronized
    fun connect(proxy: ProxyEntry): Boolean {
        val nextPort = SocketUtils.findAvailableTcpPort(INTERNAL_PROXY_SERVER_PORT_BASE)
        disconnectIfNecessary()

        if (log.isTraceEnabled) {
            log.trace("Starting forward server on {} with {}", nextPort, proxy.display)
        }

        try {
            lock.withLock {
                val serverConfig = HttpProxyServerConfig(
                        numBossGroupThreads,
                        numWorkerGroupThreads,
                        proxy,
                        numProxyGroupThreads
                )

                val server = ForwardServer(serverConfig)
                val thread = Thread { server.start(nextPort) }
                thread.isDaemon = true
                thread.start()

                waitUntilOnline(nextPort)

                forwardServer = server
                forwardServerThread = thread

                ++numTotalConnects
                port = nextPort
                proxyEntry.set(proxy)
                connected.signalAll()
            }

            if (log.isInfoEnabled) {
                log.info("Forward server is started on port {} with {}", nextPort, proxy.display)
                val script = TEST_SCRIPT.replace("{port}", nextPort.toString())
                log.info("Test script: \n$script")
            }
        } catch (e: TimeoutException) {
            log.error("Timeout to wait for forward server on port {}", nextPort)
        } catch (e: Exception) {
            log.error("Failed to start forward server", e)
        }

        return isOnline
    }

    @Synchronized
    fun disconnect(): ProxyEntry? {
        lock.withLock {
            val proxy = proxyEntry.getAndSet(null)
            val proxyServerConfig = forwardServer?.proxyServerConfig

            if (proxy != null && proxyServerConfig != null) {
                log.info("Disconnecting proxy {} {} ...", proxy.display, proxy.metadata)

                forwardServer?.use { it.close() }
                forwardServerThread?.interrupt()
                forwardServerThread?.join(threadJoinTimeout.toMillis())
                forwardServer = null
                forwardServerThread = null

                // waitUntilOffline(proxyConfig.port)
                disconnected.signalAll()
            }

            return proxy
        }
    }

    @Synchronized
    override fun close() {
        if (closed.compareAndSet(false, true)) {
            disconnect()
        }
    }

    @Synchronized
    private fun disconnectIfNecessary() {
        lock.withLock {
            if (isOnline) {
                val proxy = proxyEntry.get()
                val thread = Thread { disconnect() }
                thread.isDaemon = true
                thread.start()

                log.debug("Waiting for proxy to disconnect | {}", proxy)
                val signaled = disconnected.await(20, TimeUnit.SECONDS)
                if (!signaled) {
                    log.warn("Timeout to wait for proxy to disconnect | {}", proxy)
                }

                thread.join(threadJoinTimeout.toMillis())
            }
        }
    }

    @Throws(TimeoutException::class)
    private fun waitUntilOnline(port: Int) {
        var i = 0
        val proxy = proxyEntry.get()
        while (!isClosed && !NetUtil.testNetwork("127.0.0.1", port) && !Thread.currentThread().isInterrupted) {
            if (i++ > 5) {
                log.warn("Waited {}s for proxy to be online | {}", i, proxy.display)
            }
            if (i > 20) {
                disconnect()
                throw TimeoutException("Timeout to wait for proxy to connect")
            }

            try {
                TimeUnit.SECONDS.sleep(1)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    private fun waitUntilOffline(port: Int) {
        var i = 0
        val proxy = proxyEntry.get()
        while (!isClosed && NetUtil.testNetwork("127.0.0.1", port) && !Thread.currentThread().isInterrupted) {
            if (i++ > 5) {
                log.warn("Waited {}s for proxy to be offline | {}", i, proxy.display)
            }

            try {
                TimeUnit.SECONDS.sleep(1)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }
}
