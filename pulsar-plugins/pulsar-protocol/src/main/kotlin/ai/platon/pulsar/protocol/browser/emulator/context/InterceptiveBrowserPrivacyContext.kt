package ai.platon.pulsar.protocol.browser.emulator.context

import ai.platon.pulsar.common.Strings
import ai.platon.pulsar.common.config.ImmutableConfig
import ai.platon.pulsar.common.proxy.NoProxyException
import ai.platon.pulsar.common.proxy.ProxyEntry
import ai.platon.pulsar.common.proxy.ProxyPoolManager
import ai.platon.pulsar.common.readable
import ai.platon.pulsar.crawl.BrowserInstanceId
import ai.platon.pulsar.crawl.PrivacyContext
import ai.platon.pulsar.crawl.PrivacyContextId
import ai.platon.pulsar.crawl.fetch.FetchResult
import ai.platon.pulsar.crawl.fetch.FetchTask
import ai.platon.pulsar.protocol.browser.driver.ManagedWebDriver
import ai.platon.pulsar.protocol.browser.driver.WebDriverPoolManager

/**
 * The privacy context, the context is closed if privacy is leaked
 * */
open class InterceptiveBrowserPrivacyContext(
        val driverPoolManager: WebDriverPoolManager,
        val proxyPoolManager: ProxyPoolManager,
        conf: ImmutableConfig
): PrivacyContext(PrivacyContextId(generateBaseDir()), conf) {

    private val browserInstanceId: BrowserInstanceId
    private var proxyEntry: ProxyEntry? = null
    private val driverContext: WebDriverContext
    private val proxyContext: ProxyContext

    init {
        if (proxyPoolManager.isEnabled) {
            val proxyPool = proxyPoolManager.proxyPool
            proxyEntry = proxyPoolManager.activeProxyEntries.computeIfAbsent(id.dataDir) {
                proxyPool.take() ?: throw NoProxyException("No proxy found in pool ${proxyPool.javaClass.simpleName} | $proxyPool")
            }
            proxyEntry?.startWork()
        }

        browserInstanceId = BrowserInstanceId.resolve(id.dataDir).apply { proxyServer = proxyEntry?.hostPort }
        driverContext = WebDriverContext(browserInstanceId, driverPoolManager, conf)
        proxyContext = ProxyContext(proxyEntry, proxyPoolManager, driverContext, conf)
    }

    open suspend fun run(task: FetchTask, browseFun: suspend (FetchTask, ManagedWebDriver) -> FetchResult): FetchResult {
        if (!isActive) return FetchResult.privacyRetry(task)
        beforeRun(task)
        val result = proxyContext.takeIf { it.isEnabled }?.run(task, browseFun)
                ?:driverContext.run(task, browseFun)
        return result.also { afterRun(it) }
    }

    override fun report() {
        log.info("Privacy context #{} has lived for {}" +
                " | success: {}({} pages/s) | small: {}({}) | traffic: {}({}/s) | tasks: {} total run: {}",
                sequence, elapsedTime.readable(),
                numSuccesses, String.format("%.2f", throughput),
                numSmallPages, String.format("%.1f%%", 100 * smallPageRate),
                Strings.readableBytes(systemNetworkBytesRecv), Strings.readableBytes(networkSpeed),
                numTasks, numTotalRun
        )

        if (smallPageRate > 0.5) {
            log.warn("Privacy context #{} is disqualified, too many small pages: {}({})",
                    sequence, numSmallPages, String.format("%.1f%%", 100 * smallPageRate))
        }

        // 0 to disable
        if (throughput < 0) {
            log.warn("Privacy context #{} is disqualified, it's expected 120 pages in 120 seconds at least", sequence)
            // check the zombie context list, if the context keeps go bad, the proxy provider is bad
        }
    }

    /**
     * Block until all the drivers are closed and the proxy is offline
     * */
    override fun close() {
        if (closed.compareAndSet(false, true)) {
            driverContext.close()
            proxyContext.close()
            report()
        }
    }

    private fun beforeRun(task: FetchTask) {
        numTasks.incrementAndGet()
    }

    private fun afterRun(result: FetchResult) {
        numTotalRun.incrementAndGet()
        if (result.status.isSuccess) {
            numSuccesses.incrementAndGet()
        }

        if (result.isSmall) {
            numSmallPages.incrementAndGet()
        }
    }
}