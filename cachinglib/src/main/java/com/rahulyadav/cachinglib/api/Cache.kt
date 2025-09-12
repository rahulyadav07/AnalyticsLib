package com.rahulyadav.cachinglib.api

import android.content.Context
import com.rahulyadav.cachinglib.core.CacheManager
import com.rahulyadav.cachinglib.core.CacheResult
import com.rahulyadav.cachinglib.core.CacheStats
import com.rahulyadav.cachinglib.config.CacheConfig

/**
 * Simple Cache Library.
 * Just get, put, remove, and clear - that's it!
 */
object Cache {
    
    private var manager: CacheManager<Any>? = null
    
    /**
     * Initialize once in your Application class
     */
    fun initialize(context: Context) {
        if (manager == null) {
            val config = CacheConfig.Builder<Any>(context)
                .memoryCacheSize(100) // 100 items in memory
                .diskCacheSize(1000)  // 1000 items on disk
                .build()
            manager = CacheManager(config)
        }
    }
    
    /**
     * Get value from cache
     */
    suspend fun <T> get(key: String): T? {
        val result = manager?.get(key) ?: return null
        return when (result) {
            is CacheResult.Success -> result.value as? T
            is CacheResult.Miss -> null
            is CacheResult.Error -> null
        }
    }
    
    /**
     * Put value into cache
     */
    suspend fun <T> put(key: String, value: T): Boolean {
        val result = manager?.put(key, value as Any) ?: return false
        return when (result) {
            is CacheResult.Success -> true
            is CacheResult.Error -> false
            is CacheResult.Miss -> false
        }
    }
    
    /**
     * Remove value from cache
     */
    suspend fun remove(key: String): Boolean {
        val result = manager?.remove(key) ?: return false
        return when (result) {
            is CacheResult.Success -> true
            is CacheResult.Error -> false
            is CacheResult.Miss -> false
        }
    }
    
    /**
     * Clear all caches
     */
    suspend fun clear(): Boolean {
        val result = manager?.clear() ?: return false
        return when (result) {
            is CacheResult.Success -> true
            is CacheResult.Error -> false
            is CacheResult.Miss -> false
        }
    }
    
    /**
     * Check if key exists in cache
     */
    suspend fun contains(key: String): Boolean {
        return manager?.contains(key) ?: false
    }
    
    /**
     * Get cache statistics
     */
    suspend fun getStats(): CacheStats? {
        return manager?.getStats()
    }
}
