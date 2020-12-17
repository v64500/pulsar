package ai.platon.pulsar.common.collect

import ai.platon.pulsar.common.AppContext
import ai.platon.pulsar.common.Priority
import ai.platon.pulsar.common.sleep
import ai.platon.pulsar.common.url.CrawlableFatLink
import ai.platon.pulsar.common.url.FatLink
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

open class InfinitePauseDataCollector<T>(
        val pause: Duration = Duration.ofSeconds(1),
        val sleeper: () -> Unit = { sleep(pause) },
        priority: Priority = Priority.LOWEST
): AbstractPriorityDataCollector<T>(priority) {
    override var name: String = "InfinitePauseDC"

    override fun hasMore() = true

    override fun collectTo(sink: MutableCollection<T>): Int {
        sleeper()
        return 0
    }
}

interface CrawlableFatLinkCollector {
    val fatLinks: Map<String, CrawlableFatLink>

    fun remove(url: String): CrawlableFatLink?
    fun removeAll(urls: Iterable<String>): Int = urls.count { remove(it) != null }

    fun remove(fatLink: FatLink): CrawlableFatLink?
    fun removeAll(fatLinks: List<FatLink>): Int = fatLinks.count { remove(it) != null }
}

open class MultiSourceDataCollector<E>(
        val collectors: MutableList<AbstractPriorityDataCollector<E>>,
        priority: Priority = Priority.NORMAL
): AbstractPriorityDataCollector<E>(priority) {

    override var name = "MultiSourceDC"

    private val isActive get() = AppContext.isActive
    private val roundCounter = AtomicInteger()
    private val collectedCounter = AtomicInteger()

    val round get() = roundCounter.get()
    val totalCollected get() = collectedCounter.get()

    constructor(vararg thatCollectors: AbstractPriorityDataCollector<E>, priority: Priority = Priority.NORMAL):
            this(arrayListOf(*thatCollectors), priority)

    override fun hasMore() = collectors.any { it.hasMore() }

    override fun collectTo(sink: MutableCollection<E>): Int {
        roundCounter.incrementAndGet()

        var collected = 0
        val sortedCollectors = collectors.sortedBy { it.priority }
        while (isActive && collected == 0 && hasMore()) {
            sortedCollectors.forEach {
                if (isActive && collected == 0 && it.hasMore()) {
                    collected += it.collectTo(sink)
                }
            }
        }

        return collected
    }
}
