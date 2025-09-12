package com.rahulyadav.imagedownlinglib.strategy

import android.widget.ImageView
import com.rahulyadav.imagedownlinglib.strategy.ImageCache
import com.rahulyadav.imagedownlinglib.strategy.NetworkClient
import com.rahulyadav.imagedownlinglib.strategy.ImageDecoder

/**
 * Main interface for image loading operations.
 * Follows Facade pattern - provides a simple interface to complex subsystems.
 * Follows Dependency Inversion Principle - depends on abstractions, not concretions.
 */
interface  ImageLoader {
    
    /**
     * Load image from URL into ImageView
     * @param url The image URL
     * @param imageView The target ImageView
     * @param callback Optional callback for result handling
     */
    fun load(url: String, imageView: ImageView, callback: ImageCallback? = null)
    
    /**
     * Load image from URL and return result via callback
     * @param url The image URL
     * @param callback Callback for result handling
     */
    fun load(url: String, callback: ImageCallback)
    
    /**
     * Load image with custom request configuration
     * @param request The image request configuration
     */
    fun load(request: ImageRequest)
    
    /**
     * Cancel loading for the given URL
     * @param url The URL to cancel loading for
     */
    fun cancel(url: String)
    
    /**
     * Cancel all pending requests
     */
    fun cancelAll()
    
    /**
     * Clear all caches
     */
    suspend fun clearCache()
    
    /**
     * Get cache statistics
     */
    suspend fun getCacheStats(): CacheStats
}

/**
 * Cache statistics data class
 */
data class CacheStats(
    val memoryCacheSize: Long,
    val memoryCacheMaxSize: Long,
    val diskCacheSize: Long,
    val diskCacheMaxSize: Long
)
