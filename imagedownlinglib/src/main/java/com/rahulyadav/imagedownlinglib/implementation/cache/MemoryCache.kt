package com.rahulyadav.imagedownlinglib.implementation.cache

import android.graphics.Bitmap
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.rahulyadav.imagedownlinglib.strategy.ImageCache

/**
 * Memory cache implementation using LruCache.
 * Follows Single Responsibility Principle - only handles memory caching.
 * Thread-safe implementation using coroutines.
 */
class MemoryCache(maxSize: Long) : ImageCache {
    
    private val cache = LruCache<String, Bitmap>((maxSize / 1024).toInt()){ _, bitmap ->
        bitmap.byteCount
    }
    
    override suspend fun get(key: String): Bitmap? = withContext(Dispatchers.IO) {
        cache.get(key)
    }
    
    override suspend fun put(key: String, bitmap: Bitmap) = withContext(Dispatchers.IO) {
        cache.put(key, bitmap)
    }
    
    override suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        cache.remove(key)
        Unit
    }
    
    override suspend fun clear() = withContext(Dispatchers.IO) {
        cache.evictAll()
        Unit
    }
    
    override suspend fun size(): Long = withContext(Dispatchers.IO) {
        cache.size().toLong() * 1024 // Convert KB to bytes
    }
    
    override fun maxSize(): Long = cache.maxSize() * 1024L // Convert KB to bytes
}
