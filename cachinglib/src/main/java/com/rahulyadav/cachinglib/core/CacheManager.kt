package com.rahulyadav.cachinglib.core

import com.rahulyadav.cachinglib.strategy.Cache
import com.rahulyadav.cachinglib.config.CacheConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Main cache manager implementation.
 * Follows Facade pattern - provides simple interface to complex caching subsystems.
 * Follows Single Responsibility Principle - orchestrates cache operations.
 * Thread-safe implementation using coroutines.
 */
class CacheManager<T>(
    private val config: CacheConfig<T>
) {
    
    /**
     * Get value from cache (memory first, then disk)
     */
    suspend fun get(key: String): CacheResult<T> = withContext(Dispatchers.IO) {
        try {
            // Try memory cache first
            if (config.enableMemoryCache) {
                val memoryValue = config.memoryCache.get(key)
                if (memoryValue != null) {
                    return@withContext CacheResult.Success(memoryValue)
                }
            }
            
            // Try disk cache
            if (config.enableDiskCache) {
                val diskValue = config.diskCache.get(key)
                if (diskValue != null) {
                    // Store in memory cache for future access
                    if (config.enableMemoryCache) {
                        config.memoryCache.put(key, diskValue)
                    }
                    return@withContext CacheResult.Success(diskValue)
                }
            }
            
            CacheResult.Miss()
        } catch (e: Exception) {
            CacheResult.Error(e)
        }
    }
    
    /**
     * Put value into cache (both memory and disk)
     */
    suspend fun put(key: String, value: T): CacheResult<Unit> = withContext(Dispatchers.IO) {
        try {
            // Store in memory cache
            if (config.enableMemoryCache) {
                config.memoryCache.put(key, value)
            }
            
            // Store in disk cache
            if (config.enableDiskCache) {
                config.diskCache.put(key, value)
            }
            
            CacheResult.Success(Unit)
        } catch (e: Exception) {
            CacheResult.Error(e)
        }
    }
    
    /**
     * Remove value from cache
     */
    suspend fun remove(key: String): CacheResult<Unit> = withContext(Dispatchers.IO) {
        try {
            if (config.enableMemoryCache) {
                config.memoryCache.remove(key)
            }
            
            if (config.enableDiskCache) {
                config.diskCache.remove(key)
            }
            
            CacheResult.Success(Unit)
        } catch (e: Exception) {
            CacheResult.Error(e)
        }
    }
    
    /**
     * Clear all caches
     */
    suspend fun clear(): CacheResult<Unit> = withContext(Dispatchers.IO) {
        try {
            if (config.enableMemoryCache) {
                config.memoryCache.clear()
            }
            
            if (config.enableDiskCache) {
                config.diskCache.clear()
            }
            
            CacheResult.Success(Unit)
        } catch (e: Exception) {
            CacheResult.Error(e)
        }
    }
    
    /**
     * Check if key exists in cache
     */
    suspend fun contains(key: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (config.enableMemoryCache && config.memoryCache.contains(key)) {
                return@withContext true
            }
            
            if (config.enableDiskCache && config.diskCache.contains(key)) {
                return@withContext true
            }
            
            false
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get cache statistics
     */
    suspend fun getStats(): CacheStats = withContext(Dispatchers.IO) {
        CacheStats(
            memoryCacheSize = if (config.enableMemoryCache) config.memoryCache.size() else 0,
            memoryCacheMaxSize = if (config.enableMemoryCache) config.memoryCache.maxSize() else 0,
            diskCacheSize = if (config.enableDiskCache) config.diskCache.size() else 0,
            diskCacheMaxSize = if (config.enableDiskCache) config.diskCache.maxSize() else 0
        )
    }
}

/**
 * Cache statistics data class
 */
data class CacheStats(
    val memoryCacheSize: Int,
    val memoryCacheMaxSize: Int,
    val diskCacheSize: Int,
    val diskCacheMaxSize: Int
)
