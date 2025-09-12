package com.rahulyadav.cachinglib.implementation

import com.rahulyadav.cachinglib.strategy.Cache

/**
 * No-op cache implementation for when caching is disabled.
 * Follows Null Object pattern - provides default behavior when caching is disabled.
 * Follows Single Responsibility Principle - only handles no-cache scenario.
 */
class NoCache<T> : Cache<T> {
    override suspend fun get(key: String): T? = null
    override suspend fun put(key: String, value: T) { /* No-op */ }
    override suspend fun remove(key: String) { /* No-op */ }
    override suspend fun clear() { /* No-op */ }
    override suspend fun contains(key: String): Boolean = false
    override suspend fun size(): Int = 0
    override fun maxSize(): Int = 0
}
