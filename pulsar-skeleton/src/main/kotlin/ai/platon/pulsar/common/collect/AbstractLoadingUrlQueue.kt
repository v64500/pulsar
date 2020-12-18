package ai.platon.pulsar.common.collect

import ai.platon.pulsar.common.Priority
import ai.platon.pulsar.common.url.UrlAware
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet

abstract class AbstractLoadingUrlQueue(
        val loader: ExternalUrlLoader,
        val group: Int = 0,
        val priority: Int = Priority.NORMAL.value
): AbstractQueue<UrlAware>() {
    protected val cache = ConcurrentSkipListSet<UrlAware>()

    constructor(loader: ExternalUrlLoader, group: Int, priority: Priority = Priority.NORMAL)
            : this(loader, group, priority.value)

    override fun add(url: UrlAware) = offer(url)

    @Synchronized
    override fun offer(url: UrlAware): Boolean {
        return if (cache.size < loader.cacheSize) {
            cache.add(url)
        } else {
            loader.save(url)
            true
        }
    }

    override fun iterator(): MutableIterator<UrlAware> = cache.iterator()

    override fun peek(): UrlAware? {
        var url = cache.firstOrNull()
        while (url == null && loader.hasMore()) {
            loader.loadTo(cache, group, priority)
            url = cache.firstOrNull()
        }
        return url
    }

    override fun poll(): UrlAware? {
        peek()
        return cache.pollFirst()
    }

    override val size: Int get() = cache.size
}