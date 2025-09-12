package com.rahulyadav.imagedownlinglib.config

import android.content.Context
import com.rahulyadav.imagedownlinglib.strategy.ImageCache
import com.rahulyadav.imagedownlinglib.implementation.cache.MemoryCache
import com.rahulyadav.imagedownlinglib.implementation.cache.DiskCache
import com.rahulyadav.imagedownlinglib.implementation.cache.NoCache
import com.rahulyadav.imagedownlinglib.strategy.NetworkClient
import com.rahulyadav.imagedownlinglib.implementation.network.OkHttpNetworkClient
import com.rahulyadav.imagedownlinglib.strategy.ImageDecoder
import com.rahulyadav.imagedownlinglib.implementation.decoder.BitmapDecoder

/**
 * Configuration class for ImageLoader.
 * Follows Builder pattern for flexible configuration.
 * Follows Single Responsibility Principle - only handles configuration.
 */
data class ImageLoaderConfig(
    val memoryCache: ImageCache,
    val diskCache: ImageCache,
    val networkClient: NetworkClient,
    val imageDecoder: ImageDecoder,
    val timeoutSeconds: Long,
    val enableLogging: Boolean
) {
    
    /**
     * Builder class for creating ImageLoaderConfig instances.
     */
    class Builder(private val context: Context) {
        private var memoryCache: ImageCache? = null
        private var diskCache: ImageCache? = null
        private var networkClient: NetworkClient? = null
        private var imageDecoder: ImageDecoder? = null
        private var timeoutSeconds: Long = 30
        private var enableLogging: Boolean = false
        
        /**
         * Set memory cache configuration
         */
        fun memoryCacheSize(sizeInBytes: Long) = apply {
            this.memoryCache = MemoryCache(sizeInBytes)
        }
        
        /**
         * Disable memory cache
         */
        fun disableMemoryCache() = apply {
            this.memoryCache = NoCache()
        }
        
        /**
         * Set disk cache configuration
         */
        fun diskCacheSize(sizeInBytes: Long) = apply {
            this.diskCache = DiskCache(context, sizeInBytes)
        }
        
        /**
         * Disable disk cache
         */
        fun disableDiskCache() = apply {
            this.diskCache = NoCache()
        }
        
        /**
         * Set custom memory cache implementation
         */
        fun memoryCache(cache: ImageCache) = apply {
            this.memoryCache = cache
        }
        
        /**
         * Set custom disk cache implementation
         */
        fun diskCache(cache: ImageCache) = apply {
            this.diskCache = cache
        }
        
        /**
         * Set custom network client
         */
        fun networkClient(client: NetworkClient) = apply {
            this.networkClient = client
        }
        
        /**
         * Set custom image decoder
         */
        fun imageDecoder(decoder: ImageDecoder) = apply {
            this.imageDecoder = decoder
        }
        
        /**
         * Set network timeout
         */
        fun timeout(seconds: Long) = apply {
            this.timeoutSeconds = seconds
        }
        
        /**
         * Enable logging
         */
        fun enableLogging() = apply {
            this.enableLogging = true
        }
        
        /**
         * Build the configuration
         */
        fun build(): ImageLoaderConfig {
            return ImageLoaderConfig(
                memoryCache = memoryCache ?: MemoryCache(50 * 1024 * 1024), // 50MB default
                diskCache = diskCache ?: DiskCache(context, 200 * 1024 * 1024), // 200MB default
                networkClient = networkClient ?: OkHttpNetworkClient(timeoutSeconds),
                imageDecoder = imageDecoder ?: BitmapDecoder(),
                timeoutSeconds = timeoutSeconds,
                enableLogging = enableLogging
            )
        }
    }
}
