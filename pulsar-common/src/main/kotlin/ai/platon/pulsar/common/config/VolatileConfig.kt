package ai.platon.pulsar.common.config

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Created by vincent on 18-1-17.
 * Copyright @ 2013-2017 Platon AI. All rights reserved
 */
open class VolatileConfig : MutableConfig {
    var fallbackConfig: ImmutableConfig? = null

    private val ttls: MutableMap<String, Int> = ConcurrentHashMap()
    val variables: MutableMap<String, Any> = ConcurrentHashMap()

    constructor() : super(false)

    constructor(fallbackConfig: ImmutableConfig?) : super(false) {
        this.fallbackConfig = fallbackConfig
        if (fallbackConfig is VolatileConfig) {
            ttls.putAll(fallbackConfig.ttls)
            variables.putAll(fallbackConfig.variables)
        }
    }

    fun reset() {
        ttls.clear()
        variables.clear()
        super.clear()
    }

    override fun get(name: String, defaultValue: String): String {
        val value = super.get(name)
        if (value != null) {
            if (!isExpired(name)) {
                return super.get(name, defaultValue)
            } else {
                if (LOG.isTraceEnabled) {
                    LOG.trace("Session config (with default) {} is expired", name)
                }
                ttls.remove(name)
                super.unset(name)
            }
        }
        return if (fallbackConfig != null) fallbackConfig!![name, defaultValue] else defaultValue
    }

    override fun get(name: String): String? {
        Objects.requireNonNull(name)
        val value = super.get(name)
        if (value != null) {
            if (!isExpired(name)) {
                return value
            } else {
                if (AbstractConfiguration.LOG.isTraceEnabled) {
                    AbstractConfiguration.LOG.trace("Session config {} is expired", name)
                }
                ttls.remove(name)
                super.unset(name)
            }
        }
        return if (fallbackConfig != null) fallbackConfig!![name] else null
    }

    operator fun set(name: String, value: String, ttl: Int) {
        setTTL(name, ttl)
        super.set(name, value)
    }

    fun getAndSet(name: String, value: String, ttl: Int): String? {
        val old = get(name)
        if (old != null) {
            this[name, value] = ttl
        }
        return old
    }

    fun getTTL(name: String): Int {
        return ttls.getOrDefault(name, Int.MAX_VALUE)
    }

    open fun setTTL(name: String, ttl: Int) {
        if (ttl > 0) {
            ttls[name] = ttl
        } else {
            ttls.remove(name)
            super.unset(name)
        }
    }

    fun <T : Any> putBean(bean: T): Any? {
        return putBean(bean.javaClass.name, bean)
    }

    fun <T : Any> putBean(name: String, bean: T): Any? {
        return variables.put(name, bean)
    }

    fun <T> getBean(bean: Class<T>): T? {
        val obj = variables.values.firstOrNull { bean.isAssignableFrom(it.javaClass) }
        return obj as? T
    }

    fun <T : Any> getBean(bean: KClass<T>): T? {
        val obj = variables.values.firstOrNull { bean.java.isAssignableFrom(it.javaClass) }
        return obj as? T
    }

    fun <T> getBean(name: String, bean: Class<T>): T? {
        val obj = variables[name]
        return if (obj != null && bean.isAssignableFrom(obj.javaClass)) {
            obj as T
        } else null
    }

    fun <T : Any> getBean(name: String, bean: KClass<T>): T? {
        return getBean(name, bean.java)
    }

    fun <T : Any> removeBean(bean: T): Any? {
        return variables.remove(bean.javaClass.name)
    }

    fun <T : Any> removeBean(name: String): Any? {
        return variables.remove(name)
    }

    fun getVariable(name: String?): Any? {
        return variables[name]
    }

    fun setVariable(name: String, value: Any) {
        variables[name] = value
    }

    open fun isExpired(key: String): Boolean {
        return false
    }

    companion object {
        val EMPTY = VolatileConfig()
    }
}
