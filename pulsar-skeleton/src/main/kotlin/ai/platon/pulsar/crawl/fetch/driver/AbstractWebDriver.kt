package ai.platon.pulsar.crawl.fetch.driver

import ai.platon.pulsar.browser.common.BrowserSettings
import ai.platon.pulsar.common.urls.UrlUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URL
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.random.Random

abstract class AbstractWebDriver(
    override val browser: Browser,
    override val id: Int = instanceSequencer.incrementAndGet()
): Comparable<AbstractWebDriver>, WebDriver {
    companion object {
        val instanceSequencer = AtomicInteger()
    }

    override var idleTimeout: Duration = Duration.ofMinutes(10)

    override var waitForTimeout = Duration.ofMinutes(1)

    override val name get() = javaClass.simpleName + "-" + id

    override val delayPolicy: (String) -> Long get() = { type ->
        when (type) {
            "gap" -> 500L + Random.nextInt(500)
            "click" -> 500L + Random.nextInt(1000)
            "type" -> 50L + Random.nextInt(500)
            "mouseWheel" -> 800L + Random.nextInt(500)
            "dragAndDrop" -> 800L + Random.nextInt(500)
            "waitForNavigation" -> 500L
            "waitForSelector" -> 500L
            else -> 100L + Random.nextInt(500)
        }
    }

    /**
     * The url to navigate
     * The browser might redirect, so it might not be the same with currentUrl()
     * */
    override var navigateEntry: NavigateEntry = NavigateEntry("")

    override val navigateHistory: MutableList<NavigateEntry> = Collections.synchronizedList(mutableListOf())

    /**
     * The url to navigate
     * The browser might redirect, so it might not be the same with currentUrl()
     * */
//    override var url: String = navigateEntry.url
    /**
     * Whether the web driver has javascript support
     * */
    override val supportJavascript: Boolean = true
    /**
     * Whether the web page source is mocked
     * */
    override val isMockedPageSource: Boolean = false
    /**
     * Driver status
     * */
    override val status = AtomicReference(WebDriver.Status.UNKNOWN)

    override var lastActiveTime: Instant = Instant.now()

    override val isWorking get() = status.get().isWorking
    val isNotWorking get() = !isWorking
    override val isRetired get() = status.get().isRetired
    override val isCanceled get() = status.get().isCanceled
    override val isQuit get() = status.get().isQuit
    override val isFree get() = status.get().isFree
    override val isCrashed get() = status.get().isCrashed

    private var jsoupSession: Connection? = null

    override fun free() = status.set(WebDriver.Status.FREE)
    override fun startWork() = status.set(WebDriver.Status.WORKING)
    override fun retire() = status.set(WebDriver.Status.RETIRED)
    override fun cancel() {
        if (status.compareAndSet(WebDriver.Status.WORKING, WebDriver.Status.CANCELED)) {
            // stop()
        }
    }

    @Throws(WebDriverException::class)
    override suspend fun navigateTo(url: String) = navigateTo(NavigateEntry(url))

    @Throws(WebDriverException::class)
    override suspend fun waitForSelector(selector: String, timeoutMillis: Long): Long =
        waitForSelector(selector, Duration.ofMillis(timeoutMillis))

    @Throws(WebDriverException::class)
    override suspend fun waitForSelector(selector: String): Long = waitForSelector(selector, waitForTimeout)

    @Throws(WebDriverException::class)
    override suspend fun waitForNavigation(): Long = waitForNavigation(Duration.ofSeconds(10))

    @Throws(WebDriverException::class)
    override suspend fun waitForNavigation(timeoutMillis: Long): Long = waitForNavigation(Duration.ofMillis(timeoutMillis))

    override suspend fun evaluateSilently(expression: String): Any? =
        takeIf { isWorking }?.runCatching { evaluate(expression) }

    @Throws(WebDriverException::class)
    override suspend fun scrollDown(count: Int) {
        repeat(count) {
            evaluate("__pulsar_utils__.scrollDown()")
        }
    }

    @Throws(WebDriverException::class)
    override suspend fun scrollUp(count: Int) {
        evaluate("__pulsar_utils__.scrollUp()")
    }

    @Throws(WebDriverException::class)
    override suspend fun scrollToTop() {
        evaluate("__pulsar_utils__.scrollToTop()")
    }

    @Throws(WebDriverException::class)
    override suspend fun scrollToBottom() {
        evaluate("__pulsar_utils__.scrollToBottom()")
    }

    @Throws(WebDriverException::class)
    override suspend fun scrollToMiddle(ratio: Float) {
        evaluate("__pulsar_utils__.scrollToMiddle($ratio)")
    }

    @Throws(WebDriverException::class)
    override suspend fun clickNthAnchor(n: Int, rootSelector: String): String? {
        val result = evaluate("__pulsar_utils__.clickNthAnchor($n, '$rootSelector')")
        return result?.toString()
    }

    @Throws(WebDriverException::class)
    override suspend fun outerHTML(selector: String): String? {
        val result = evaluate("__pulsar_utils__.outerHTML('$selector')")
        return result?.toString()
    }

    @Throws(WebDriverException::class)
    override suspend fun firstText(selector: String): String? {
        val result = evaluate("__pulsar_utils__.firstText('$selector')")
        return result?.toString()
    }

    @Throws(WebDriverException::class)
    override suspend fun allTexts(selector: String): List<String> {
        val result = evaluate("__pulsar_utils__.allTexts('$selector')")
        return result?.toString()?.split("\n")?.toList() ?: listOf()
    }

    @Throws(WebDriverException::class)
    override suspend fun firstAttr(selector: String, attrName: String): String? {
        val result = evaluate("__pulsar_utils__.firstAttr('$selector', '$attrName')")
        return result?.toString()
    }

    @Throws(WebDriverException::class)
    override suspend fun allAttrs(selector: String, attrName: String): List<String> {
        val result = evaluate("__pulsar_utils__.allAttrs('$selector', '$attrName')")
        return result?.toString()?.split("\n")?.toList() ?: listOf()
    }

    /**
     * Create a new session with the same context of the browser: headers, cookies, proxy, etc.
     * The browser should be initialized by opening a page before the session is created.
     * */
    @Throws(WebDriverException::class)
    override suspend fun newSession(): Connection {
        val headers = mainRequestHeaders().entries.associate { it.key to it.value.toString() }
        val cookies = getCookies()
        val userAgent = BrowserSettings.randomUserAgent()

        val httpTimeout = Duration.ofSeconds(20)
        val session = Jsoup.newSession()
            .timeout(httpTimeout.toMillis().toInt())
            .userAgent(userAgent)
            .headers(headers)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)

        if (cookies.isNotEmpty()) {
            session.cookies(cookies.first())
        }

        // Since the browser uses the system proxy (by default),
        // so the http connection should also use the system proxy
        val proxy = browser.id.proxyServer ?: System.getenv("http_proxy")
        if (proxy != null && UrlUtils.isValidUrl(proxy)) {
            val u = URL(proxy)
            session.proxy(u.host, u.port)
        }

        return session
    }

    @Throws(IOException::class)
    override suspend fun loadResource(url: String): Connection.Response? {
        if (jsoupSession == null) {
            jsoupSession = newSession()
        }

        val response = withContext(Dispatchers.IO) {
            jsoupSession?.newRequest()?.url(url)?.execute()
        }

        return response
    }

    override fun equals(other: Any?): Boolean = other is AbstractWebDriver && other.id == this.id

    override fun hashCode(): Int = id

    override fun compareTo(other: AbstractWebDriver): Int = id - other.id

    override fun toString(): String = sessionId?.let { "#$id-$sessionId" }?:"#$id(closed)"
}
