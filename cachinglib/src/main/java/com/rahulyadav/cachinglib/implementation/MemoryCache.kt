package com.rahulyadav.cachinglib.implementation

import android.util.LruCache
import com.rahulyadav.cachinglib.strategy.Cache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Memory cache implementation using LruCache.
 * Follows Single Responsibility Principle - only handles memory caching.
 * Thread-safe implementation using coroutines.
 */
class MemoryCache<T>(
    maxSize: Int = 100
) : Cache<T> {
    
    private val cache = LruCache<String, T>(maxSize)
    
    override suspend fun get(key: String): T? = withContext(Dispatchers.IO) {
        cache.get(key)
    }
    
    override suspend fun put(key: String, value: T) = withContext(Dispatchers.IO) {
        cache.put(key, value)
    }
    
    override suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        cache.remove(key)
        Unit
    }
    
    override suspend fun clear() = withContext(Dispatchers.IO) {
        cache.evictAll()
        Unit
    }
    
    override suspend fun contains(key: String): Boolean = withContext(Dispatchers.IO) {
        cache.get(key) != null
    }
    
    override suspend fun size(): Int = withContext(Dispatchers.IO) {
        cache.size()
    }
    
    override fun maxSize(): Int = cache.maxSize()
}
