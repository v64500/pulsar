package ai.platon.pulsar.common.proxy

import ai.platon.pulsar.common.AppPaths
import ai.platon.pulsar.common.FileCommand
import ai.platon.pulsar.common.SParser
import ai.platon.pulsar.common.Systems
import ai.platon.pulsar.common.config.AppConstants
import ai.platon.pulsar.common.config.CapabilityTypes
import ai.platon.pulsar.common.config.ImmutableConfig
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

open class ProxyManager(
        private val conf: ImmutableConfig
): AutoCloseable {
    val numRunningTasks = AtomicInteger()
    var lastActiveTime = Instant.now()
    var idleTimeout = conf.getDuration(CapabilityTypes.PROXY_IDLE_TIMEOUT, Duration.ofMinutes(5))
    var idleCount = 0
    var idleTime = Duration.ZERO
    val closed = AtomicBoolean()

    var statusString: String = ""
    var verbose = false

    open val localPort = -1
    open val currentProxyEntry: ProxyEntry? = null
    open val isEnabled = false
    val isDisabled get() = !isEnabled
    val isClosed get() = closed.get()

    open fun start() {}

    /**
     * Run the task despite the proxy manager is disabled, it it's disabled, call the innovation directly
     * */
    open suspend fun <R> submit(task: suspend () -> R): R = if (isDisabled) task() else submit0(task)

    /**
     * Run the task despite the proxy manager is disabled, it it's disabled, call the innovation directly
     * */
    open fun <R> run(task: () -> R): R = if (isDisabled) task() else run0(task)

    /**
     * Run the task in the proxy manager
     * */
    protected suspend fun <R> submit0(task: suspend () -> R): R {
        beforeRun()

        return try {
            numRunningTasks.incrementAndGet()
            task()
        } finally {
            lastActiveTime = Instant.now()
            numRunningTasks.decrementAndGet()
        }
    }

    /**
     * Run the task in the proxy manager
     * */
    protected fun <R> run0(task: () -> R): R {
        beforeRun()

        return try {
            numRunningTasks.incrementAndGet()
            task()
        } finally {
            lastActiveTime = Instant.now()
            numRunningTasks.decrementAndGet()
        }
    }

    open fun waitUntilOnline(): Boolean = false

    open fun changeProxyIfOnline(excludedProxy: ProxyEntry, ban: Boolean) {}

    override fun toString(): String = statusString

    override fun close() {}

    private fun beforeRun() {
        if (isClosed || isDisabled) {
            throw ProxyDisabledException("Proxy manager is " + if (isClosed) "closed" else "disabled")
        }

        idleTime = Duration.ZERO

        if (!waitUntilOnline()) {
            throw NoProxyException("Failed to wait for an online proxy")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProxyManager::class.java)
        private const val PROXY_PROVIDER_FILE_NAME = "proxy.providers.txt"
        private val DEFAULT_PROXY_PROVIDER_FILES = arrayOf(AppConstants.TMP_DIR, AppConstants.USER_HOME)
                .map { Paths.get(it, PROXY_PROVIDER_FILE_NAME) }

        private val PROXY_FILE_WATCH_INTERVAL = Duration.ofSeconds(30)
        private var providerDirLastWatchTime = Instant.EPOCH
        private var numEnabledProviderFiles = 0L

        init {
            DEFAULT_PROXY_PROVIDER_FILES.mapNotNull { it.takeIf { Files.exists(it) } }.forEach {
                FileUtils.copyFileToDirectory(it.toFile(), AppPaths.AVAILABLE_PROVIDER_DIR.toFile())
            }

            if (Systems.getProperty(CapabilityTypes.PROXY_ENABLE_DEFAULT_PROVIDERS, false)) {
                enableDefaultProviders()
            }
        }

        fun hasEnabledProvider(): Boolean {
            val now = Instant.now()
            synchronized(ProxyManager::class.java) {
                if (Duration.between(providerDirLastWatchTime, now) > PROXY_FILE_WATCH_INTERVAL) {
                    providerDirLastWatchTime = now
                    numEnabledProviderFiles = try {
                        Files.list(AppPaths.ENABLED_PROVIDER_DIR).filter { Files.isRegularFile(it) }.count()
                    } catch (e: Throwable) { 0 }
                }
            }

            return numEnabledProviderFiles > 0
        }

        /**
         * Proxy system can be enabled/disabled at runtime
         * */
        fun isProxyEnabled(): Boolean {
            if (FileCommand.check(AppConstants.CMD_ENABLE_PROXY)) {
                return true
            }

            // explicit set system environment property
            val useProxy = System.getProperty(CapabilityTypes.PROXY_USE_PROXY)
            if (useProxy != null) {
                when (useProxy) {
                    "yes" -> return true
                    "no" -> return false
                }
            }

            // if no one set the proxy availability explicitly, but we have providers, use it
            return hasEnabledProvider()
        }

        fun enableDefaultProviders() {
            DEFAULT_PROXY_PROVIDER_FILES.mapNotNull { it.takeIf { Files.exists(it) } }.forEach { enableProvider(it) }
        }

        fun enableProvider(providerPath: Path) {
            val filename = providerPath.fileName
            arrayOf(AppPaths.AVAILABLE_PROVIDER_DIR, AppPaths.ENABLED_PROVIDER_DIR)
                    .map { it.resolve(filename) }
                    .filterNot { Files.exists(it) }
                    .forEach { Files.copy(providerPath, it) }
        }

        fun disableProviders() {
            Files.list(AppPaths.ENABLED_PROVIDER_DIR).filter { Files.isRegularFile(it) }.forEach { Files.delete(it) }
        }
    }
}
