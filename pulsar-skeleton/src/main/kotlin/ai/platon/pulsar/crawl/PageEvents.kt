package ai.platon.pulsar.crawl

import ai.platon.pulsar.common.*
import ai.platon.pulsar.common.Runtimes.randomDelay
import ai.platon.pulsar.common.urls.UrlAware
import ai.platon.pulsar.crawl.fetch.FetchResult
import ai.platon.pulsar.crawl.fetch.driver.NavigateEntry
import ai.platon.pulsar.crawl.fetch.driver.WebDriver
import ai.platon.pulsar.crawl.fetch.privacy.PrivacyContext
import ai.platon.pulsar.dom.FeaturedDocument
import ai.platon.pulsar.persist.PageDatum
import ai.platon.pulsar.persist.WebPage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.random.Random

interface EventHandler {
    val name: String
    val isRelevant: Boolean
}

/**
 * Event listener interface.
 */
interface EventListener {

}

abstract class AbstractEventHandler: EventHandler {
    override val name: String = ""
    override val isRelevant: Boolean = true
}

interface EventHandlerPipeline {
    val size: Int
    val isEmpty: Boolean get() = size == 0
    val isNotEmpty: Boolean get() = !isEmpty

    fun remove(handler: Any): Boolean
    fun clear()
}

abstract class VoidEventHandler: () -> Unit, AbstractEventHandler() {
    abstract override operator fun invoke()
}

abstract class UrlAwareHandler: (UrlAware) -> UrlAware?, AbstractEventHandler() {
    abstract override operator fun invoke(url: UrlAware): UrlAware?
}

abstract class UrlAwareFilter: (UrlAware) -> UrlAware?, AbstractEventHandler() {
    abstract override operator fun invoke(url: UrlAware): UrlAware?
}

abstract class UrlHandler: (String) -> String?, AbstractEventHandler() {
    abstract override operator fun invoke(url: String): String?
}

abstract class UrlFilter: (String) -> String?, AbstractEventHandler() {
    abstract override operator fun invoke(url: String): String?
}

abstract class WebPageHandler: (WebPage) -> Any?, AbstractEventHandler() {
    abstract override operator fun invoke(page: WebPage): Any?
}

abstract class UrlAwareWebPageHandler: (UrlAware, WebPage?) -> Any?, AbstractEventHandler() {
    abstract override operator fun invoke(url: UrlAware, page: WebPage?): Any?
}

abstract class HTMLDocumentHandler: (WebPage, FeaturedDocument) -> Any?, AbstractEventHandler() {
    abstract override operator fun invoke(page: WebPage, document: FeaturedDocument): Any?
}

abstract class PrivacyContextHandler: (PrivacyContext) -> Any?, AbstractEventHandler() {
    abstract override operator fun invoke(privacyContext: PrivacyContext): Any?
}

abstract class WebDriverHandler: (WebDriver) -> Any?, AbstractEventHandler() {
    abstract override operator fun invoke(driver: WebDriver): Any?
    abstract suspend fun invokeDeferred(page: WebPage, driver: WebDriver): Any?
}

abstract class WebPageWebDriverHandler: (WebPage, WebDriver) -> Any?, AbstractEventHandler() {
    abstract override operator fun invoke(page: WebPage, driver: WebDriver): Any?
    abstract suspend fun invokeDeferred(page: WebPage, driver: WebDriver): Any?
}

abstract class WebDriverFetchResultHandler: (WebPage, WebDriver) -> FetchResult?, AbstractEventHandler() {
    abstract override operator fun invoke(page: WebPage, driver: WebDriver): FetchResult?
    abstract suspend fun invokeDeferred(page: WebPage, driver: WebDriver): FetchResult?
}

class VoidEventHandlerPipeline: VoidEventHandler(), EventHandlerPipeline {
    private val registeredHandlers = CopyOnWriteArrayList<VoidEventHandler>()

    override val size: Int
        get() = registeredHandlers.size

    fun addFirst(handler: () -> Unit) = addFirst(object: VoidEventHandler() {
        override fun invoke() = handler()
    })

    fun addFirst(handler: VoidEventHandler): VoidEventHandlerPipeline {
        registeredHandlers.add(0, handler)
        return this
    }

    fun addLast(handler: () -> Unit) = addLast(object: VoidEventHandler() {
        override fun invoke() = handler()
    })

    fun addLast(handler: VoidEventHandler): VoidEventHandlerPipeline {
        registeredHandlers.add(handler)
        return this
    }

    override fun remove(handler: Any) = registeredHandlers.remove(handler)

    override fun clear() = registeredHandlers.clear()

    override operator fun invoke() {
        registeredHandlers.asSequence().filter { it.isRelevant }.forEach { it() }
    }
}

class UrlAwareHandlerPipeline: UrlAwareHandler(), EventHandlerPipeline {
    private val registeredHandlers = CopyOnWriteArrayList<UrlAwareHandler>()

    override val size: Int
        get() = registeredHandlers.size

    fun addFirst(handler: (UrlAware) -> UrlAware?) = addFirst(object: UrlAwareHandler() {
        override fun invoke(url: UrlAware) = handler.invoke(url)
    })

    fun addFirst(handler: UrlAwareHandler): UrlAwareHandlerPipeline {
        registeredHandlers.add(0, handler)
        return this
    }

    fun addLast(handler: (UrlAware) -> UrlAware?) = addLast(object: UrlAwareHandler() {
        override fun invoke(url: UrlAware) = handler.invoke(url)
    })

    fun addLast(handler: UrlAwareHandler): UrlAwareHandlerPipeline {
        registeredHandlers.add(handler)
        return this
    }

    override fun remove(handler: Any) = registeredHandlers.remove(handler)

    override fun clear() = registeredHandlers.clear()

    override operator fun invoke(url: UrlAware): UrlAware? {
        var result: UrlAware? = null
        registeredHandlers.asSequence().filter { it.isRelevant }.forEach { result = it(url) }
        return result
    }
}

class UrlAwareFilterPipeline: UrlAwareFilter(), EventHandlerPipeline {
    private val registeredHandlers = CopyOnWriteArrayList<UrlAwareFilter>()

    override val size: Int
        get() = registeredHandlers.size

    fun addFirst(handler: (UrlAware) -> UrlAware?) = addFirst(object: UrlAwareFilter() {
        override fun invoke(url: UrlAware) = handler(url)
    })

    fun addFirst(handler: UrlAwareFilter): UrlAwareFilterPipeline {
        registeredHandlers.add(0, handler)
        return this
    }

    fun addLast(handler: (UrlAware) -> UrlAware?) = addLast(object: UrlAwareFilter() {
        override fun invoke(url: UrlAware) = handler(url)
    })

    fun addLast(handler: UrlAwareFilter): UrlAwareFilterPipeline {
        registeredHandlers.add(handler)
        return this
    }

    override fun remove(handler: Any) = registeredHandlers.remove(handler)

    override fun clear() = registeredHandlers.clear()

    override operator fun invoke(url: UrlAware): UrlAware? {
        var result: UrlAware? = url
        registeredHandlers.asSequence().filter { it.isRelevant }.forEach { result = it(url) }
        return result
    }
}

class UrlFilterPipeline: UrlFilter(), EventHandlerPipeline {
    private val registeredHandlers = CopyOnWriteArrayList<UrlFilter>()

    override val size: Int
        get() = registeredHandlers.size

    fun addFirst(handler: UrlFilter): UrlFilterPipeline {
        registeredHandlers.add(0, handler)
        return this
    }

    fun addFirst(handler: (String) -> String?) = addFirst(object : UrlFilter() {
        override fun invoke(url: String) = handler(url)
    })

    fun addLast(handler: UrlFilter): UrlFilterPipeline {
        registeredHandlers.add(handler)
        return this
    }

    fun addLast(handler: (String) -> String?) = addLast(object : UrlFilter() {
        override fun invoke(url: String) = handler(url)
    })

    fun remove(handler: (String) -> String) {
        registeredHandlers.removeIf { it == handler }
    }

    override fun remove(handler: Any) = registeredHandlers.remove(handler)

    override fun clear() = registeredHandlers.clear()

    override operator fun invoke(url: String): String? {
        var result: String? = url
        registeredHandlers.asSequence().filter { it.isRelevant }.forEach { result = it(url) }
        return result
    }
}

class UrlHandlerPipeline: UrlHandler(), EventHandlerPipeline {
    private val registeredHandlers = CopyOnWriteArrayList<UrlHandler>()

    override val size: Int
        get() = registeredHandlers.size

    fun addFirst(handler: (String) -> String?) = addFirst(object : UrlHandler() {
        override fun invoke(url: String) = handler(url)
    })

    fun addFirst(handler: UrlHandler): UrlHandlerPipeline {
        registeredHandlers.add(0, handler)
        return this
    }

    fun addLast(handler: (String) -> String?) = addLast(object : UrlHandler() {
        override fun invoke(url: String) = handler(url)
    })

    fun addLast(handler: UrlHandler): UrlHandlerPipeline {
        registeredHandlers.add(handler)
        return this
    }

    fun remove(handler: (String) -> String?) {
        registeredHandlers.removeIf { it == handler }
    }

    override fun remove(handler: Any) = registeredHandlers.remove(handler)

    override fun clear() = registeredHandlers.clear()

    override operator fun invoke(url: String): String? {
        var result: String? = null
        registeredHandlers.asSequence().filter { it.isRelevant }.forEach { result = it(url) }
        return result
    }
}

class WebPageHandlerPipeline: WebPageHandler(), EventHandlerPipeline {
    private val registeredHandlers = CopyOnWriteArrayList<WebPageHandler>()

    override val size: Int
        get() = registeredHandlers.size

    fun addFirst(handler: WebPageHandler): WebPageHandlerPipeline {
        registeredHandlers.add(0, handler)
        return this
    }

    fun addFirst(handler: (WebPage) -> Any?) = addFirst(object : WebPageHandler() {
        override fun invoke(page: WebPage) = handler(page)
    })

    fun addLast(handler: (WebPage) -> Any?) = addLast(object: WebPageHandler() {
        override fun invoke(page: WebPage) = handler(page)
    })

    fun addLast(handler: WebPageHandler): WebPageHandlerPipeline {
        registeredHandlers += handler
        return this
    }

    override fun remove(handler: Any) = registeredHandlers.remove(handler)

    override fun clear() = registeredHandlers.clear()

    override operator fun invoke(page: WebPage): Any? {
        var result: Any? = null
        registeredHandlers.asSequence().filter { it.isRelevant }.forEach { result = it(page) }
        return result
    }
}

class UrlAwareWebPageHandlerPipeline: UrlAwareWebPageHandler(), EventHandlerPipeline {
    private val registeredHandlers = CopyOnWriteArrayList<UrlAwareWebPageHandler>()

    override val size: Int
        get() = registeredHandlers.size

    fun addFirst(handler: UrlAwareWebPageHandler): UrlAwareWebPageHandlerPipeline {
        registeredHandlers.add(0, handler)
        return this
    }

    fun addFirst(handler: (UrlAware, WebPage?) -> Any?) = addFirst(object : UrlAwareWebPageHandler() {
        override fun invoke(url: UrlAware, page: WebPage?) = handler(url, page)
    })

    fun addLast(handler: UrlAwareWebPageHandler): UrlAwareWebPageHandlerPipeline {
        registeredHandlers.add(handler)
        return this
    }

    fun addLast(handler: (UrlAware, WebPage?) -> Any?) = addLast(object : UrlAwareWebPageHandler() {
        override fun invoke(url: UrlAware, page: WebPage?) = handler(url, page)
    })

    override fun remove(handler: Any) = registeredHandlers.remove(handler)

    override fun clear() = registeredHandlers.clear()

    override operator fun invoke(url: UrlAware, page: WebPage?): Any? {
        var result: Any? = null
        registeredHandlers.asSequence().filter { it.isRelevant }.forEach { result = it(url, page) }
        return result
    }
}

class HTMLDocumentHandlerPipeline: HTMLDocumentHandler(), EventHandlerPipeline {
    private val registeredHandlers = CopyOnWriteArrayList<HTMLDocumentHandler>()

    override val size: Int
        get() = registeredHandlers.size

    fun addFirst(handler: (WebPage, FeaturedDocument) -> Any?) = addFirst(object : HTMLDocumentHandler() {
        override fun invoke(page: WebPage, document: FeaturedDocument) = handler(page, document)
    })

    fun addFirst(handler: HTMLDocumentHandler): HTMLDocumentHandlerPipeline {
        registeredHandlers.add(0, handler)
        return this
    }

    fun addLast(handler: (WebPage, FeaturedDocument) -> Any?) = addLast(object : HTMLDocumentHandler() {
        override fun invoke(page: WebPage, document: FeaturedDocument) = handler(page, document)
    })

    fun addLast(handler: HTMLDocumentHandler): HTMLDocumentHandlerPipeline {
        registeredHandlers.add(handler)
        return this
    }

    override fun remove(handler: Any) = registeredHandlers.remove(handler)

    override fun clear() = registeredHandlers.clear()

    override fun invoke(page: WebPage, document: FeaturedDocument): Any? {
        var result: Any? = null
        registeredHandlers.asSequence().filter { it.isRelevant }.forEach { result = it(page, document) }
        return result
    }
}

interface LoadEvent {
    val onFilter: UrlFilterPipeline

    val onNormalize: UrlFilterPipeline

    val onWillLoad: UrlHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onWillLoad"))
    val onBeforeLoad: UrlHandlerPipeline get() = onWillLoad

    val onWillFetch: WebPageHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onWillFetch"))
    val onBeforeFetch: WebPageHandlerPipeline get() = onWillFetch

    val onWillLaunchBrowser: WebPageHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onWillLaunchBrowser"))
    val onBeforeBrowserLaunch: WebPageHandlerPipeline get() = onWillLaunchBrowser

    val onBrowserLaunched: WebPageWebDriverHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onBrowserLaunched"))
    val onAfterBrowserLaunch: WebPageWebDriverHandlerPipeline get() = onBrowserLaunched

    val onFetched: WebPageHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onFetched"))
    val onAfterFetch: WebPageHandlerPipeline get() = onFetched

    val onWillParse: WebPageHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onWillParseHTMLDocument"))
    val onBeforeParse: WebPageHandlerPipeline get() = onWillParse

    val onWillParseHTMLDocument: WebPageHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onWillParseHTMLDocument"))
    val onBeforeHtmlParse: WebPageHandlerPipeline get() = onWillParseHTMLDocument

    val onWillExtractData: WebPageHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onWillExtract"))
    val onBeforeExtractData: WebPageHandlerPipeline get() = onWillExtractData

    val onDataExtracted: HTMLDocumentHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onExtracted"))
    val onAfterExtract: HTMLDocumentHandlerPipeline get() = onDataExtracted

    val onHTMLDocumentParsed: HTMLDocumentHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onHTMLDocumentParsed"))
    val onAfterHtmlParse: HTMLDocumentHandlerPipeline get() = onHTMLDocumentParsed

    val onParsed: WebPageHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onParsed"))
    val onAfterParse: WebPageHandlerPipeline get() = onParsed

    val onLoaded: WebPageHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onLoaded"))
    val onAfterLoad: WebPageHandlerPipeline get() = onLoaded

    fun combine(other: LoadEvent): LoadEvent
}

abstract class AbstractLoadEvent(
    override val onFilter: UrlFilterPipeline = UrlFilterPipeline(),
    override val onNormalize: UrlFilterPipeline = UrlFilterPipeline(),
    override val onWillLoad: UrlHandlerPipeline = UrlHandlerPipeline(),
    override val onWillFetch: WebPageHandlerPipeline = WebPageHandlerPipeline(),
    override val onWillLaunchBrowser: WebPageHandlerPipeline = WebPageHandlerPipeline(),
    override val onBrowserLaunched: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline(),
    override val onFetched: WebPageHandlerPipeline = WebPageHandlerPipeline(),
    override val onWillParse: WebPageHandlerPipeline = WebPageHandlerPipeline(),
    override val onWillParseHTMLDocument: WebPageHandlerPipeline = WebPageHandlerPipeline(),
    override val onWillExtractData: WebPageHandlerPipeline = WebPageHandlerPipeline(),
    override val onDataExtracted: HTMLDocumentHandlerPipeline = HTMLDocumentHandlerPipeline(),
    override val onHTMLDocumentParsed: HTMLDocumentHandlerPipeline = HTMLDocumentHandlerPipeline(),
    override val onParsed: WebPageHandlerPipeline = WebPageHandlerPipeline(),
    override val onLoaded: WebPageHandlerPipeline = WebPageHandlerPipeline()
): LoadEvent {

    override fun combine(other: LoadEvent): AbstractLoadEvent {
        onFilter.addLast(other.onFilter)
        onNormalize.addLast(other.onNormalize)
        onWillLoad.addLast(other.onWillLoad)
        onWillFetch.addLast(other.onWillFetch)
        onWillLaunchBrowser.addLast(other.onWillLaunchBrowser)
        onBrowserLaunched.addLast(other.onBrowserLaunched)
        onFetched.addLast(other.onFetched)
        onWillParse.addLast(other.onWillParse)
        onWillParseHTMLDocument.addLast(other.onWillParseHTMLDocument)
        onWillExtractData.addLast(other.onWillExtractData)
        onDataExtracted.addLast(other.onDataExtracted)
        onHTMLDocumentParsed.addLast(other.onHTMLDocumentParsed)
        onParsed.addLast(other.onParsed)
        onLoaded.addLast(other.onLoaded)

        return this
    }
}

open class DefaultLoadEvent(
    val rpa: BrowseRPA = DefaultBrowseRPA()
): AbstractLoadEvent() {
    override val onBrowserLaunched = WebPageWebDriverHandlerPipeline()
        .addLast { page, driver ->
            rpa.warnUpBrowser(page, driver)
        }
}

abstract class AbstractWebPageWebDriverHandler: WebPageWebDriverHandler() {
    private val logger = getLogger(AbstractWebPageWebDriverHandler::class)

    open val delayPolicy: (String) -> Long get() = { type ->
        when (type) {
            "click" -> 500L + Random.nextInt(500)
            "type" -> 500L + Random.nextInt(500)
            else -> 100L + Random.nextInt(500)
        }
    }

    open var verbose = false

    override val name: String = ""

    override fun invoke(page: WebPage, driver: WebDriver): Any? {
        return runBlocking { invokeDeferred(page, driver) }
    }

    protected suspend fun evaluate(driver: WebDriver, expressions: Iterable<String>): Any? {
        var value: Any? = null
        val validExpressions = expressions
            .mapNotNull { it.trim().takeIf { it.isNotBlank() } }
            .filterNot { it.startsWith("// ") }
        validExpressions.forEach {
            logger.takeIf { verbose }?.info("Evaluate expression >>>$it<<<")
            val v = evaluate(driver, it)
            if (v is String) {
                val s = Strings.stripNonPrintableChar(v)
                logger.takeIf { verbose }?.info("Result >>>$s<<<")
            } else if (v is Int || v is Long) {
                logger.takeIf { verbose }?.info("Result >>>$v<<<")
            }
            value = v
        }
        return value
    }

    protected suspend fun evaluate(driver: WebDriver, expression: String): Any? {
        delayPolicy("evaluate").takeIf { it > 0 }?.let { delay(it) }
        return driver.evaluate(expression)
    }
}

open class EmptyWebPageWebDriverHandler: AbstractWebPageWebDriverHandler() {
    override suspend fun invokeDeferred(page: WebPage, driver: WebDriver): Any? {
        return null
    }
}

/**
 * @see [EmulateEvent].
 *
 * About emulate, simulate, mimic and imitate:
 * 1. Emulate is usually used with someone as an object.
 * 2. Simulate has the idea of copying something so that the copy pretends to be the original thing.
 * 3. Mimic, a person who imitate mannerisms of others.
 * 4. Imitate is the most general of the four words, can be used in all the three senses.
 * */
interface SimulateEvent {
    @Deprecated("Old fashioned name", ReplaceWith("onWillFetch"))
    val onBeforeFetch: WebPageWebDriverHandlerPipeline get() = onWillFetch
    val onWillFetch: WebPageWebDriverHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onFetched"))
    val onAfterFetch: WebPageWebDriverHandlerPipeline get() = onFetched
    val onFetched: WebPageWebDriverHandlerPipeline

    @Deprecated("Old fashioned name", ReplaceWith("onWillNavigate"))
    val onBeforeNavigate: WebPageWebDriverHandlerPipeline get() = onWillNavigate
    val onWillNavigate: WebPageWebDriverHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onNavigated"))
    val onAfterNavigate: WebPageWebDriverHandlerPipeline get() = onNavigated
    val onNavigated: WebPageWebDriverHandlerPipeline

    @Deprecated("Old fashioned name", ReplaceWith("onWillCheckDOMState"))
    val onBeforeCheckDOMState: WebPageWebDriverHandlerPipeline get() = onWillCheckDOMState
    val onWillCheckDOMState: WebPageWebDriverHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onDOMStateChecked"))
    val onAfterCheckDOMState: WebPageWebDriverHandlerPipeline get() = onDOMStateChecked
    val onDOMStateChecked: WebPageWebDriverHandlerPipeline

    @Deprecated("Old fashioned name", ReplaceWith("onWillComputeFeature"))
    val onBeforeComputeFeature: WebPageWebDriverHandlerPipeline get() = onWillComputeFeature
    val onWillComputeFeature: WebPageWebDriverHandlerPipeline
    @Deprecated("Old fashioned name", ReplaceWith("onFeatureComputed"))
    val onAfterComputeFeature: WebPageWebDriverHandlerPipeline get() = onFeatureComputed
    val onFeatureComputed: WebPageWebDriverHandlerPipeline

    val onWillInteract: WebPageWebDriverHandlerPipeline
    val onDidInteract: WebPageWebDriverHandlerPipeline

    val onWillStopTab: WebPageWebDriverHandlerPipeline
    val onTabStopped: WebPageWebDriverHandlerPipeline

    fun combine(other: SimulateEvent): SimulateEvent
}

abstract class AbstractSimulateEvent: SimulateEvent {
    open val delayPolicy: (String) -> Long get() = { type ->
        when (type) {
            "click" -> 500L + Random.nextInt(500)
            "type" -> 500L + Random.nextInt(500)
            else -> 100L + Random.nextInt(500)
        }
    }

    open var verbose = false

    protected suspend fun smartDelay() = delay(delayPolicy(""))

    protected suspend fun smartDelay(type: String) = delay(delayPolicy(type))

    override val onWillFetch: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline()
    override val onFetched: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline()

    override val onWillNavigate: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline()
    override val onNavigated: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline()

    override val onWillCheckDOMState: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline()
    override val onDOMStateChecked: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline()

    override val onWillComputeFeature: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline()
    override val onFeatureComputed: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline()

    override val onWillInteract: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline()
    override val onDidInteract: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline()

    override val onWillStopTab: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline()
    override val onTabStopped: WebPageWebDriverHandlerPipeline = WebPageWebDriverHandlerPipeline()

    override fun combine(other: SimulateEvent): SimulateEvent {
        onWillFetch.addLast(other.onWillFetch)
        onFetched.addLast(other.onFetched)

        onWillNavigate.addLast(other.onWillNavigate)
        onNavigated.addLast(other.onNavigated)

        onWillCheckDOMState.addLast(other.onWillCheckDOMState)
        onDOMStateChecked.addLast(other.onDOMStateChecked)
        onWillComputeFeature.addLast(other.onWillComputeFeature)
        onFeatureComputed.addLast(other.onFeatureComputed)

        onWillInteract.addLast(other.onWillInteract)
        onDidInteract.addLast(other.onDidInteract)
        onWillStopTab.addLast(other.onWillStopTab)
        onTabStopped.addLast(other.onTabStopped)

        return this
    }
}

class WebPageWebDriverHandlerPipeline: AbstractWebPageWebDriverHandler(), EventHandlerPipeline {
    private val registeredHandlers = CopyOnWriteArrayList<WebPageWebDriverHandler>()

    override val size: Int
        get() = registeredHandlers.size

    fun addFirst(handler: suspend (WebPage, WebDriver) -> Any?): WebPageWebDriverHandlerPipeline {
        registeredHandlers.add(0, object: AbstractWebPageWebDriverHandler() {
            override suspend fun invokeDeferred(page: WebPage, driver: WebDriver): Any? {
                return handler(page, driver)
            }
        })
        return this
    }

    fun addFirst(handler: WebPageWebDriverHandler): WebPageWebDriverHandlerPipeline {
        registeredHandlers.add(0, handler)
        return this
    }

    fun addLast(handler: suspend (WebPage, WebDriver) -> Any?): WebPageWebDriverHandlerPipeline {
        registeredHandlers.add(object: AbstractWebPageWebDriverHandler() {
            override suspend fun invokeDeferred(page: WebPage, driver: WebDriver): Any? {
                return handler(page, driver)
            }
        })
        return this
    }

    fun addLast(handler: WebPageWebDriverHandler): WebPageWebDriverHandlerPipeline {
        registeredHandlers.add(handler)
        return this
    }

    override fun remove(handler: Any) = registeredHandlers.remove(handler)

    override fun clear() = registeredHandlers.clear()

    override suspend fun invokeDeferred(page: WebPage, driver: WebDriver): Any? {
        var result: Any? = null
        registeredHandlers.asSequence().filter { it.isRelevant }.forEach { result = it.invokeDeferred(page, driver) }
        return result
    }
}

abstract class AbstractWebDriverFetchResultHandler: WebDriverFetchResultHandler() {
    private val logger = getLogger(AbstractWebDriverFetchResultHandler::class)

    override fun invoke(page: WebPage, driver: WebDriver): FetchResult? {
        return runBlocking { invokeDeferred(page, driver) }
    }
}

class WebDriverFetchResultHandlerPipeline: AbstractWebDriverFetchResultHandler(), EventHandlerPipeline {
    private val registeredHandlers = CopyOnWriteArrayList<WebDriverFetchResultHandler>()

    override val size: Int
        get() = registeredHandlers.size

    fun addFirst(handler: suspend (WebPage, WebDriver) -> FetchResult?): WebDriverFetchResultHandlerPipeline {
        registeredHandlers.add(0, object: AbstractWebDriverFetchResultHandler() {
            override suspend fun invokeDeferred(page: WebPage, driver: WebDriver): FetchResult? {
                return handler(page, driver)
            }
        })
        return this
    }

    fun addFirst(handler: WebDriverFetchResultHandler): WebDriverFetchResultHandlerPipeline {
        registeredHandlers.add(0, handler)
        return this
    }

    fun addLast(handler: suspend (WebPage, WebDriver) -> FetchResult?): WebDriverFetchResultHandlerPipeline {
        registeredHandlers.add(object: AbstractWebDriverFetchResultHandler() {
            override suspend fun invokeDeferred(page: WebPage, driver: WebDriver): FetchResult? {
                return handler(page, driver)
            }
        })
        return this
    }

    fun addLast(handler: WebDriverFetchResultHandler): WebDriverFetchResultHandlerPipeline {
        registeredHandlers.add(handler)
        return this
    }

    override fun remove(handler: Any) = registeredHandlers.remove(handler)

    override fun clear() = registeredHandlers.clear()

    override suspend fun invokeDeferred(page: WebPage, driver: WebDriver): FetchResult? {
        var result: FetchResult? = null
        registeredHandlers.asSequence().filter { it.isRelevant }
            .forEach { result = it.invokeDeferred(page, driver) }
        return result
    }
}

class ExpressionSimulateEvent(
    val beforeComputeExpressions: Iterable<String> = listOf(),
    val afterComputeExpressions: Iterable<String> = listOf()
): AbstractSimulateEvent() {
    constructor(bcExpressions: String, acExpressions2: String, delimiters: String = ";"): this(
        bcExpressions.split(delimiters), acExpressions2.split(delimiters))

    override val onWillComputeFeature = WebPageWebDriverHandlerPipeline()
        .addFirst(object: AbstractWebPageWebDriverHandler() {
            override suspend fun invokeDeferred(page: WebPage, driver: WebDriver) =
                evaluate(driver, beforeComputeExpressions)
        })

    override val onFeatureComputed = WebPageWebDriverHandlerPipeline()
        .addFirst(object: AbstractWebPageWebDriverHandler() {
            override suspend fun invokeDeferred(page: WebPage, driver: WebDriver): Any? {
                return evaluate(driver, afterComputeExpressions)
            }
        })
}

abstract class PageDatumHandler: (String, PageDatum) -> Any?, AbstractEventHandler() {
    abstract override operator fun invoke(pageSource: String, pageDatum: PageDatum): Any?
}

class PageDatumHandlerPipeline: PageDatumHandler(), EventHandlerPipeline {
    private val registeredHandlers = CopyOnWriteArrayList<PageDatumHandler>()

    override val size: Int
        get() = registeredHandlers.size

    fun addFirst(handler: PageDatumHandler): PageDatumHandlerPipeline {
        registeredHandlers.add(0, handler)
        return this
    }

    fun addFirst(handler: (String, PageDatum) -> Any?) =
        addFirst(object : PageDatumHandler() {
            override fun invoke(pageSource: String, pageDatum: PageDatum) = handler(pageSource, pageDatum)
        })

    fun addLast(handler: PageDatumHandler): PageDatumHandlerPipeline {
        registeredHandlers.add(handler)
        return this
    }

    fun addLast(handler: (String, PageDatum) -> Any?) = addLast(object : PageDatumHandler() {
        override fun invoke(pageSource: String, pageDatum: PageDatum) = handler(pageSource, pageDatum)
    })

    override fun remove(handler: Any): Boolean {
        return registeredHandlers.remove(handler)
    }

    override fun clear() {
        registeredHandlers.clear()
    }

    override operator fun invoke(pageSource: String, pageDatum: PageDatum): Any? {
        var result: Any? = null
        registeredHandlers.asSequence().filter { it.isRelevant }.forEach { result = it(pageSource, pageDatum) }
        return result
    }
}

interface BrowseRPA {
    suspend fun warnUpBrowser(page: WebPage, driver: WebDriver)
    suspend fun waitForReferrer(page: WebPage, driver: WebDriver)
    suspend fun waitForPreviousPage(page: WebPage, driver: WebDriver)
    suspend fun visit(url: String, driver: WebDriver)
}

open class DefaultBrowseRPA: BrowseRPA {
    companion object {
        const val PREV_PAGE_WILL_READY = 0
        const val PREV_PAGE_READY = 1
        const val PREV_PAGE_NEVER_READY = 2
    }

    private val isActive get() = AppContext.isActive
    private val logger = getLogger(this)

    override suspend fun warnUpBrowser(page: WebPage, driver: WebDriver) {
        page.referrer?.let { visit(it, driver) }
    }

    override suspend fun waitForReferrer(page: WebPage, driver: WebDriver) {
        val referrer = page.referrer ?: return
        val referrerVisited = driver.browser.navigateHistory.any { it.url == referrer }
        if (!referrerVisited) {
            logger.debug("Visiting the referrer | {}", referrer)
            visit(referrer, driver)
        }
    }

    override suspend fun waitForPreviousPage(page: WebPage, driver: WebDriver) {
        var tick = 0
        var checkState = checkPreviousPage(driver)
        while (tick++ <= 180 && checkState.code == PREV_PAGE_WILL_READY) {
            if (checkState.message.isBlank()) {
                // No previous page, the browser has just started, don't crowd into.
                randomDelay(1_000, 10_000)
                break
            }

            // The last page does not load completely, wait for it.
            val shouldReport = (tick > 150 && tick % 10 == 0)
            if (shouldReport) {
                val urlToWait = checkState.message
                logger.info("Waiting for page | {} | {} <- {}", tick, urlToWait, page.url)
            }

            delay(1000L)
            checkState = checkPreviousPage(driver)
        }
    }

    override suspend fun visit(url: String, driver: WebDriver) {
        val display = driver.browser.id.display
        logger.info("Visiting with browser #{} | {}", display, url)

        driver.navigateTo(url)
        driver.waitForSelector("body")
        var n = 2 + Random.nextInt(5)
        while (n-- > 0 && isActive) {
            val deltaY = 100.0 + 20 * Random.nextInt(10)
            driver.mouseWheelDown(deltaY = deltaY)
            randomDelay(500, 500)
        }

        logger.debug("Visited | {}", url)
    }

    private fun checkPreviousPage(driver: WebDriver): CheckState {
        val navigateHistory = driver.browser.navigateHistory
        val now = Instant.now()

        val testNav = navigateHistory.lastOrNull { mayWaitFor(it, driver.navigateEntry) }

        val code = when {
            !isActive -> PREV_PAGE_NEVER_READY
            !driver.isWorking -> PREV_PAGE_NEVER_READY
            testNav == null -> PREV_PAGE_WILL_READY
            testNav.documentReadyTime > now -> PREV_PAGE_WILL_READY
            Duration.between(testNav.documentReadyTime, now).seconds > 10 -> PREV_PAGE_READY
            Duration.between(testNav.lastActiveTime, now).seconds > 60 -> PREV_PAGE_NEVER_READY
            else -> PREV_PAGE_WILL_READY
        }

        return CheckState(code, testNav?.url ?: "")
    }

    private fun mayWaitFor(currentEntry: NavigateEntry, testEntry: NavigateEntry): Boolean {
        val now = Instant.now()

        val may = testEntry.pageId > 0
                && !testEntry.stopped
                && testEntry.createTime < currentEntry.createTime
                && Duration.between(testEntry.lastActiveTime, now).seconds < 30

        return may
    }
}

class DefaultSimulateEvent(
    val rpa: BrowseRPA = DefaultBrowseRPA()
): AbstractSimulateEvent() {

    override val onWillFetch = WebPageWebDriverHandlerPipeline().addLast { page, driver ->
        rpa.waitForReferrer(page, driver)
        rpa.waitForPreviousPage(page, driver)
    }
}

interface CrawlEvent {
    val onFilter: UrlAwareFilterPipeline

    val onNormalize: UrlAwareFilterPipeline

    val onWillLoad: UrlAwareHandlerPipeline

    val onLoad: UrlAwareHandlerPipeline

    val onLoaded: UrlAwareWebPageHandlerPipeline

    fun combine(other: CrawlEvent): CrawlEvent
}

abstract class AbstractCrawlEvent(
    override val onFilter: UrlAwareFilterPipeline = UrlAwareFilterPipeline(),
    override val onNormalize: UrlAwareFilterPipeline = UrlAwareFilterPipeline(),
    override val onWillLoad: UrlAwareHandlerPipeline = UrlAwareHandlerPipeline(),
    override val onLoad: UrlAwareHandlerPipeline = UrlAwareHandlerPipeline(),
    override val onLoaded: UrlAwareWebPageHandlerPipeline = UrlAwareWebPageHandlerPipeline()
): CrawlEvent {
    override fun combine(other: CrawlEvent): CrawlEvent {
        onFilter.addLast(other.onFilter)
        onNormalize.addLast(other.onNormalize)
        onWillLoad.addLast(other.onWillLoad)
        onLoad.addLast(other.onLoad)
        onLoaded.addLast(other.onLoaded)
        return this
    }
}

class DefaultCrawlEvent: AbstractCrawlEvent()

/**
 * @see [SimulateEvent]
 *
 * About emulate, simulate, mimic and imitate:
 * 1. Emulate is usually used with someone as an object.
 * 2. Simulate has the idea of copying something so that the copy pretends to be the original thing.
 * 3. Mimic, a person who imitate mannerisms of others.
 * 4. Imitate is the most general of the four words, can be used in all the three senses.
 * */
interface EmulateEvent {
    val onSniffPageCategory: PageDatumHandlerPipeline
    val onCheckHtmlIntegrity: PageDatumHandlerPipeline

    fun combine(other: EmulateEvent): EmulateEvent
}

abstract class AbstractEmulateEvent(
    override val onSniffPageCategory: PageDatumHandlerPipeline = PageDatumHandlerPipeline(),
    override val onCheckHtmlIntegrity: PageDatumHandlerPipeline = PageDatumHandlerPipeline(),
): EmulateEvent {
    override fun combine(other: EmulateEvent): EmulateEvent {
        onSniffPageCategory.addLast(other.onSniffPageCategory)
        onCheckHtmlIntegrity.addLast(other.onCheckHtmlIntegrity)
        return this
    }
}

class DefaultEmulateEvent: AbstractEmulateEvent() {
    override val onSniffPageCategory: PageDatumHandlerPipeline = PageDatumHandlerPipeline()
    override val onCheckHtmlIntegrity: PageDatumHandlerPipeline = PageDatumHandlerPipeline()
}

/**
 * Manage all events of a web page life cycle. The page events are visible to the end users.
 * */
interface PageEvent {
    val loadEvent: LoadEvent
    val simulateEvent: SimulateEvent
    val crawlEvent: CrawlEvent

    fun combine(other: PageEvent): PageEvent
}

abstract class AbstractPageEvent(
    override val loadEvent: AbstractLoadEvent,
    override val simulateEvent: AbstractSimulateEvent,
    override val crawlEvent: AbstractCrawlEvent
): PageEvent {

    override fun combine(other: PageEvent): PageEvent {
        loadEvent.combine(other.loadEvent)
        simulateEvent.combine(other.simulateEvent)
        crawlEvent.combine(other.crawlEvent)
        return this
    }
}

open class DefaultPageEvent(
    loadEvent: DefaultLoadEvent = DefaultLoadEvent(),
    simulateEvent: DefaultSimulateEvent = DefaultSimulateEvent(),
    crawlEvent: DefaultCrawlEvent = DefaultCrawlEvent()
): AbstractPageEvent(loadEvent, simulateEvent, crawlEvent) {

}

open class PageEventTemplate(
    loadEvent: DefaultLoadEvent = DefaultLoadEvent(),
    simulateEvent: DefaultSimulateEvent = DefaultSimulateEvent(),
    crawlEvent: DefaultCrawlEvent = DefaultCrawlEvent()
): AbstractPageEvent(loadEvent, simulateEvent, crawlEvent) {
    init {
        loadEvent.apply {
            onFilter.addLast { url ->
                url
            }
            onNormalize.addLast { url ->
                url
            }
            onWillLoad.addLast { url ->
                url
            }
            onWillFetch.addLast { page ->

            }
            onWillLaunchBrowser.addLast { page ->

            }
            onBrowserLaunched.addLast { page, driver ->

            }
            onFetched.addLast { page ->

            }
            onWillParse.addLast { page ->

            }
            onWillParseHTMLDocument.addLast { page ->

            }
            onWillExtractData.addLast { page ->

            }
            onDataExtracted.addLast { page: WebPage, document: FeaturedDocument ->

            }
            onHTMLDocumentParsed.addLast { page: WebPage, document: FeaturedDocument ->

            }
            onParsed.addLast { page ->

            }
            onLoaded.addLast { page ->

            }
        }

        simulateEvent.apply {
            onWillNavigate.addLast { page, driver ->
            }

            onNavigated.addLast { page, driver ->
            }

            onWillCheckDOMState.addLast { page, driver ->
            }

            onDOMStateChecked.addLast { page, driver ->
            }

            onWillComputeFeature.addLast { page, driver ->
            }

            onFeatureComputed.addLast { page, driver ->
            }

            onWillInteract.addLast { page, driver ->
            }

            onDidInteract.addLast { page, driver ->
            }
        }

        crawlEvent.apply {
            onFilter.addLast { url: UrlAware ->
                url
            }
            onNormalize.addLast { url: UrlAware ->
                url
            }
            onWillLoad.addLast { url: UrlAware ->
                url
            }
            onLoaded.addLast { url, page ->
                url
            }
        }
    }
}