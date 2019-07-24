package ai.platon.pulsar.common.options

import ai.platon.pulsar.common.config.CapabilityTypes
import ai.platon.pulsar.common.config.Params
import ai.platon.pulsar.common.config.VolatileConfig
import ai.platon.pulsar.persist.metadata.BrowserType
import ai.platon.pulsar.persist.metadata.FetchMode
import com.beust.jcommander.Parameter
import java.time.Duration

/**
 * Created by vincent on 19-4-24.
 * Copyright @ 2013-2017 Platon AI. All rights reserved
 *
 * The expires field supports both ISO-8601 standard and hadoop time duration format
 * ISO-8601 standard : PnDTnHnMn.nS
 * Hadoop time duration format : Valid units are : ns, us, ms, s, m, h, d.
 */
open class LoadOptions : CommonOptions {
    /** Fetch */
    @Parameter(names = ["-i", "-expires", "--expires"], converter = DurationConverter::class,
            description = "If a page is expired, it should be fetched from the internet again")
    var expires: Duration = Duration.ofDays(36500)
    // reserved
    @Parameter(names = ["-requireNotBlank"],
            description = "Keep the pages only if the required text is not blank")
    var requireNotBlank: String = ""

    /** Arrange links */
    @Parameter(names = ["-topLinks", "--top-links"], description = "Top N links")
    var topLinks = 20
    @Parameter(names = ["-outlink", "-outlinks", "-outlinkSelector", "--outlink-selector"],
            description = "The CSS selector by which the anchors in the portal page are selected to load and analyze, " +
                    "Out pages will be detected automatically if the selector is empty")
    var outlinkSelector = ""
    @Parameter(names = ["-topAnchorGroups", "--top-anchor-groups"], description = "Try the top anchor groups")
    var topAnchorGroups = 3

    @Parameter(names = ["-fetchMode", "--fetch-mode"], converter = FetchModeConverter::class,
            description = "The fetch mode, native, crowd sourcing and selenium are supported, selenium is the default")
    var fetchMode = FetchMode.SELENIUM
    @Parameter(names = ["-browser", "--browser"], converter = BrowserTypeConverter::class,
            description = "The browser to use, google chrome is the default")
    var browser = BrowserType.CHROME
    @Parameter(names = ["-scrollCount", "--scroll-count"],
            description = "The count to scroll down after a page is opened by a browser")
    var scrollCount = 5
    @Parameter(names = ["-scrollInterval", "--scroll-interval"], converter = DurationConverter::class,
            description = "The interval to scroll down after a page is opened by a browser")
    var scrollInterval: Duration = Duration.ofMillis(1000)
    @Parameter(names = ["-scriptTimeout", "--script-timeout"], converter = DurationConverter::class,
            description = "The maximum time to perform javascript injected into selenium")
    var scriptTimeout: Duration = Duration.ofSeconds(60)
    @Parameter(names = ["-pageLoadTimeout", "--page-load-timeout"], converter = DurationConverter::class,
            description = "The maximum time to wait for a page to be finished by selenium")
    var pageLoadTimeout: Duration = Duration.ofSeconds(60)

    // itemXXX should be available for all index-item pattern pages
    @Parameter(names = ["-itemBrowser"], converter = BrowserTypeConverter::class,
            description = "The browser used to visit the item pages, CHROME and NATIVE are supported")
    var itemBrowser: BrowserType = BrowserType.CHROME
    @Parameter(names = ["-itemExtractor"], converter = BrowserTypeConverter::class,
            description = "The extract used to extract item pages, use BOILERPIPE for news and DEFAULT for others")
    var itemExtractor: ItemExtractor = ItemExtractor.DEFAULT
    @Parameter(names = ["-itemExpires"], converter = DurationConverter::class,
            description = "The same as expires, but only works for item pages in harvest tasks")
    var itemExpires: Duration = Duration.ofDays(36500)
    /** Note: if scroll too many times, the page may fail to calculate the vision information */
    @Parameter(names = ["-itemScrollCount"],
            description = "The same as scrollCount, but only works for item pages in harvest tasks")
    var itemScrollCount = 5
    @Parameter(names = ["-itemScrollInterval"], converter = DurationConverter::class,
            description = "The same as scrollInterval, but only works for item pages in harvest tasks")
    var itemScrollInterval: Duration = Duration.ofMillis(500)
    @Parameter(names = ["-itemScriptTimeout"], converter = DurationConverter::class,
            description = "The same as scriptTimeout, but only works for item pages in harvest tasks")
    var itemScriptTimeout: Duration = Duration.ofSeconds(60)
    @Parameter(names = ["-itemPageLoadTimeout"], converter = DurationConverter::class,
            description = "The same as pageLoadTimeout, but only works for item pages in harvest tasks")
    var itemPageLoadTimeout: Duration = Duration.ofSeconds(60)
    @Parameter(names = ["-itemRequireNotBlank"],
            description = "Keep the item pages only if the required text is not blank")
    var itemRequireNotBlank: String = ""

    @Parameter(names = ["-shortenKey", "--shorten-key"],
            description = "Remove the query parameters when generate the page's key (reversed url)")
    var shortenKey = false
    @Parameter(names = ["-persist", "--persist"], arity = 1,
            description = "Persist fetched pages as soon as possible")
    var persist = true

    @Parameter(names = ["-retry", "--retry"],
            description = "Retry fetching the page if it's failed last time")
    var retry = false
    @Parameter(names = ["-lazyFlush", "--lazy-flush"],
            description = "If false, flush persisted pages into database as soon as possible")
    var lazyFlush = false
    @Parameter(names = ["-preferParallel", "--prefer-parallel"], arity = 1,
            description = "Parallel fetch pages whenever applicable")
    var preferParallel = true

    @Parameter(names = ["-background", "--background"], description = "Fetch the page in background")
    var background: Boolean = false
    @Parameter(names = ["-noRedirect", "--no-redirect"], description = "Do not redirect")
    var noRedirect = false
    @Parameter(names = ["-hardRedirect", "--hard-redirect"],
            description = "If false, return the original page record but the redirect target's content, " +
                    "otherwise, return the page record of the redirected target")
    var hardRedirect = false

    // parse options
    @Parameter(names = ["-ps", "-parse", "--parse"], description = "Parse the page after fetch")
    var parse = false
    @Parameter(names = ["-rpl", "-reparseLinks", "--reparse-links"], description = "Re-parse all links if the page is parsed")
    var reparseLinks = false
    @Parameter(names = ["-noNorm", "--no-link-normalizer"], arity = 1, description = "No normalizer is applied to parse links")
    var noNorm = false
    @Parameter(names = ["-noFilter", "--no-link-filter"], arity = 1, description = "No filter is applied to parse links")
    var noFilter = false

    @Parameter(names = ["-q", "-query", "--query"], description = "Extract query to extract data from")
    var query: String? = null
    @Parameter(names = ["-m", "-withModel", "--with-model"], description = "Also load page model when loading a page")
    var withModel = false
    @Parameter(names = ["-lk", "-withLinks", "--with-links"], description = "Contains links when loading page model")
    var withLinks = false
    @Parameter(names = ["-tt", "-withText", "--with-text"], description = "Contains text when loading page model")
    var withText = false

    // A volatile config is usually session scoped
    var volatileConfig: VolatileConfig? = null
        set(value) {
            if (value != null) {
                value.setInt(CapabilityTypes.FETCH_SCROLL_DOWN_COUNT, scrollCount)
                value.setDuration(CapabilityTypes.FETCH_SCROLL_DOWN_INTERVAL, scrollInterval)
                value.setDuration(CapabilityTypes.FETCH_SCRIPT_TIMEOUT, scriptTimeout)
                value.setDuration(CapabilityTypes.FETCH_PAGE_LOAD_TIMEOUT, pageLoadTimeout)
            }
            field = value
        }

    val modifiedParams: Params get() {
        val rowFormat = "%40s: %s"
        val fields = this.javaClass.declaredFields
        return fields.filter { it.annotations.any { it is Parameter } && !isDefault(it.name) }
                .onEach { it.isAccessible = true }
                .filter { it.get(this) != null }
                .associate { "-${it.name}" to it.get(this) }
                .let { Params.of(it).withRowFormat(rowFormat) }
    }

    val modifiedOptions: Map<String, Any> get() {
        val fields = this.javaClass.declaredFields
        return fields.filter { it.annotations.any { it is Parameter } && !isDefault(it.name) }
                .onEach { it.isAccessible = true }
                .filter { it.get(this) != null }
                .associate { it.name to it.get(this) }
    }

    protected constructor() {
        addObjects(this)
    }

    protected constructor(args: Array<String>) : super(args) {
        addObjects(this)
    }

    protected constructor(args: String) : super(args.trim { it <= ' ' }.replace("=".toRegex(), " ")) {
        addObjects(this)
    }

    open fun isDefault(optionName: String): Boolean {
        val value = this.javaClass.declaredFields.find { it.name == optionName }
                ?.also { it.isAccessible = true }?.get(this)
        return value == defaultParams[optionName]
    }

    override fun getParams(): Params {
        val rowFormat = "%40s: %s"
        val fields = this.javaClass.declaredFields
        return fields.filter { it.annotations.any { it is Parameter } }
                .onEach { it.isAccessible = true }
                .associate { "-${it.name}" to it.get(this) }
                .filter { it.value != null }
                .let { Params.of(it).withRowFormat(rowFormat) }
    }

    override fun toString(): String {
        return modifiedParams.withCmdLineStyle(true).withKVDelimiter(" ")
                .formatAsLine().replace("\\s+".toRegex(), " ")
    }

    /**
     * Merge this LoadOptions and other LoadOptions, return a new LoadOptions
     * */
    fun mergeModified(other: LoadOptions): LoadOptions {
        val modified = other.modifiedOptions

        this.javaClass.declaredFields.forEach {
            if (it.name in modified.keys) {
                it.set(this, modified[it.name])
            }
        }

        return this
    }

    companion object {

        val default = LoadOptions()
        val defaultParams = default.javaClass.declaredFields.associate { it.name to it.get(default) }
        val defaultArgsMap = default.toArgsMap()
        val optionNames: List<String> = default.javaClass.declaredFields
                .filter { it.annotations.any { it is Parameter } }.map { it.name }

        val helpList: List<List<String>> get() =
                LoadOptions::class.java.declaredFields
                        .mapNotNull { (it.annotations.firstOrNull { it is Parameter } as? Parameter)?.to(it) }
                        .map {
                            listOf(it.first.names.joinToString { it },
                                    it.second.type.typeName.substringAfterLast("."),
                                    defaultParams[it.second.name].toString(),
                                    it.first.description
                            )
                        }

        @JvmOverloads
        fun create(volatileConfig: VolatileConfig? = null): LoadOptions {
            val options = LoadOptions()
            options.parse()
            options.volatileConfig = volatileConfig
            return options
        }

        @JvmOverloads
        fun parse(args: String, volatileConfig: VolatileConfig? = null): LoadOptions {
            val options = LoadOptions(args)
            options.parse()
            options.volatileConfig = volatileConfig
            return options
        }

        @JvmOverloads
        fun mergeModified(o1: LoadOptions, o2: LoadOptions, volatileConfig: VolatileConfig? = null): LoadOptions {
            val options = LoadOptions()
            options.volatileConfig = volatileConfig
            return options.mergeModified(o1).mergeModified(o2)
        }
    }
}
