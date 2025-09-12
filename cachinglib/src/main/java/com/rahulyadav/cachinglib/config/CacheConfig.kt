package com.rahulyadav.cachinglib.config

import android.content.Context
import com.rahulyadav.cachinglib.strategy.Cache
import com.rahulyadav.cachinglib.strategy.Serializer
import com.rahulyadav.cachinglib.implementation.MemoryCache
import com.rahulyadav.cachinglib.implementation.DiskCache
import com.rahulyadav.cachinglib.implementation.GsonSerializer
import com.rahulyadav.cachinglib.implementation.NoCache

/**
 * Configuration class for CacheManager.
 * Follows Builder pattern for flexible configuration.
 * Follows Single Responsibility Principle - only handles configuration.
 */
data class CacheConfig<T>(
    val memoryCache: Cache<T>,
    val diskCache: Cache<T>,
    val serializer: Serializer<T>,
    val enableMemoryCache: Boolean,
    val enableDiskCache: Boolean
) {
    
    /**
     * Builder class for creating CacheConfig instances.
     */
    class Builder<T>(private val context: Context) {
        private var memoryCache: Cache<T>? = null
        private var diskCache: Cache<T>? = null
        private var serializer: Serializer<T>? = null
        private var enableMemoryCache: Boolean = true
        private var enableDiskCache: Boolean = true
        private var memoryCacheSize: Int = 100
        private var diskCacheSize: Int = 1000
        private var cacheDir: String = "cache"
        
        /**
         * Set memory cache size
         */
        fun memoryCacheSize(size: Int) = apply {
            this.memoryCacheSize = size
        }
        
        /**
         * Set disk cache size
         */
        fun diskCacheSize(size: Int) = apply {
            this.diskCacheSize = size
        }
        
        /**
         * Set cache directory name
         */
        fun cacheDirectory(name: String) = apply {
            this.cacheDir = name
        }
        
        /**
         * Enable or disable memory cache
         */
        fun enableMemoryCache(enable: Boolean) = apply {
            this.enableMemoryCache = enable
        }
        
        /**
         * Enable or disable disk cache
         */
        fun enableDiskCache(enable: Boolean) = apply {
            this.enableDiskCache = enable
        }
        
        /**
         * Set custom memory cache implementation
         */
        fun memoryCache(cache: Cache<T>) = apply {
            this.memoryCache = cache
        }
        
        /**
         * Set custom disk cache implementation
         */
        fun diskCache(cache: Cache<T>) = apply {
            this.diskCache = cache
        }
        
        /**
         * Set custom serializer
         */
        fun serializer(serializer: Serializer<T>) = apply {
            this.serializer = serializer
        }
        
        /**
         * Build the configuration
         */
        fun build(): CacheConfig<T> {
            val serializer = this.serializer ?: GsonSerializer<T>() as Serializer<T>
            
            val memoryCache = this.memoryCache ?: if (enableMemoryCache) {
                MemoryCache<T>(memoryCacheSize)
            } else {
                NoCache<T>()
            }
            
            val diskCache = this.diskCache ?: if (enableDiskCache) {
                DiskCache(context, serializer, diskCacheSize, cacheDir)
            } else {
                NoCache<T>()
            }
            
            return CacheConfig(
                memoryCache = memoryCache,
                diskCache = diskCache,
                serializer = serializer,
                enableMemoryCache = enableMemoryCache,
                enableDiskCache = enableDiskCache
            )
        }
    }
}

