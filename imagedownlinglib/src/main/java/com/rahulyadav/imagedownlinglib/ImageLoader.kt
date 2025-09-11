package com.rahulyadav.imagedownlinglib

import android.content.Context
import android.media.Image
import android.widget.ImageView
import com.rahulyadav.imagedownlinglib.core.ImageCallback
import com.rahulyadav.imagedownlinglib.core.ImageRequest
import com.rahulyadav.imagedownlinglib.config.ImageLoaderConfig
import com.rahulyadav.imagedownlinglib.factory.ImageLoaderFactory

/**
 * Main public API for the Image Loading Library.
 * Provides a simple, fluent interface for loading images.
 * Follows Facade pattern - hides complexity behind simple interface.
 */
object ImageLoader {
    
    /**
     * Initialize the ImageLoader with custom configuration
     * @param context Application context
     * @param config Custom configuration
     */
    fun initialize(context: Context, config: ImageLoaderConfig) {
        ImageLoaderFactory.initialize(context, config)
    }
    
    /**
     * Initialize the ImageLoader with default configuration
     * @param context Application context
     */
    fun initialize(context: Context) {
        ImageLoaderFactory.initialize(context)
    }
    
    /**
     * Load image from URL into ImageView
     * @param url The image URL
     * @return ImageRequestBuilder for chaining
     */
    fun load(url: String): ImageRequestBuilder {
        return ImageRequestBuilder(url)
    }
    
    /**
     * Cancel loading for the given URL
     * @param url The URL to cancel loading for
     */
    fun cancel(url: String) {
        ImageLoaderFactory.getInstance().cancel(url)
    }
    
    /**
     * Cancel all pending requests
     */
    fun cancelAll() {
        ImageLoaderFactory.getInstance().cancelAll()
    }
    
    /**
     * Clear all caches
     */
    suspend fun clearCache() {
        ImageLoaderFactory.getInstance().clearCache()
    }
    
    /**
     * Get cache statistics
     */
    suspend fun getCacheStats() = ImageLoaderFactory.getInstance().getCacheStats()
    
    /**
     * Check if ImageLoader is initialized
     */
    fun isInitialized() = ImageLoaderFactory.isInitialized()
}

/**
 * Builder class for creating image requests with fluent API.
 * Follows Builder pattern for easy configuration.
 */
class ImageRequestBuilder(private val url: String) {
    
    /**
     * Load image into ImageView
     * @param imageView The target ImageView
     * @param callback Optional callback for result handling
     */
    fun into(imageView: ImageView, callback: ImageCallback? = null) {
        val request = ImageRequest.Builder(url)
            .into(imageView)
            .callback(callback as ImageCallback)
            .build()
        ImageLoaderFactory.getInstance().load(request)
    }
    
    /**
     * Load image and return result via callback
     * @param callback Callback for result handling
     */
    fun into(callback: ImageCallback) {
        val request = ImageRequest.Builder(url)
            .callback(callback)
            .build()
        ImageLoaderFactory.getInstance().load(request)
    }
    
    /**
     * Set placeholder image resource
     * @param resourceId The placeholder resource ID
     * @return This builder for chaining
     */
    fun placeholder(resourceId: Int) = ImageRequestBuilderWithOptions(url, resourceId, null)
    
    /**
     * Set error image resource
     * @param resourceId The error resource ID
     * @return This builder for chaining
     */
    fun error(resourceId: Int) = ImageRequestBuilderWithOptions(url, null, resourceId)
}

/**
 * Builder class with placeholder and error options
 */
class ImageRequestBuilderWithOptions(
    private val url: String,
    private val placeholder: Int?,
    private val errorImage: Int?
) {
    
    /**
     * Load image into ImageView
     * @param imageView The target ImageView
     * @param callback Optional callback for result handling
     */
    fun into(imageView: ImageView, callback: (Any) -> Unit) {
        val request = ImageRequest.Builder(url)
            .apply { placeholder?.let { placeholder(it) } }
            .apply { errorImage?.let { error(it) } }
            .into(imageView)
            .callback(callback)
            .build()
        ImageLoaderFactory.getInstance().load(request)
    }
    
    /**
     * Load image and return result via callback
     * @param callback Callback for result handling
     */
    fun into(callback: ImageCallback) {
        val request = ImageRequest.Builder(url)
            .apply { placeholder?.let { placeholder(it) } }
            .apply { errorImage?.let { error(it) } }
            .callback(callback)
            .build()
        ImageLoaderFactory.getInstance().load(request)
    }
    
    /**
     * Set placeholder image resource
     * @param resourceId The placeholder resource ID
     * @return This builder for chaining
     */
    fun placeholder(resourceId: Int) = ImageRequestBuilderWithOptions(url, resourceId, errorImage)
    
    /**
     * Set error image resource
     * @param resourceId The error resource ID
     * @return This builder for chaining
     */
    fun error(resourceId: Int) = ImageRequestBuilderWithOptions(url, placeholder, resourceId)
    
    /**
     * Set target dimensions for image resizing
     * @param width Target width
     * @param height Target height
     * @return This builder for chaining
     */
    fun resize(width: Int, height: Int) = ImageRequestBuilderWithResize(url, placeholder, errorImage, width, height)
    
    /**
     * Enable center crop scaling
     * @return This builder for chaining
     */
    fun centerCrop() = ImageRequestBuilderWithScaling(url, placeholder, errorImage, true, false)
    
    /**
     * Enable center inside scaling
     * @return This builder for chaining
     */
    fun centerInside() = ImageRequestBuilderWithScaling(url, placeholder, errorImage, false, true)
}

/**
 * Builder class with resize options
 */
class ImageRequestBuilderWithResize(
    private val url: String,
    private val placeholder: Int?,
    private val errorImage: Int?,
    private val width: Int,
    private val height: Int
) {
    
    /**
     * Load image into ImageView
     * @param imageView The target ImageView
     * @param callback Optional callback for result handling
     */
    fun into(imageView: ImageView, callback: ImageCallback? = null) {
        val request = ImageRequest.Builder(url)
            .apply { placeholder?.let { placeholder(it) } }
            .apply { errorImage?.let { error(it) } }
            .resize(width, height)
            .into(imageView)
            .callback(callback as ImageCallback)
            .build()
        ImageLoaderFactory.getInstance().load(request)
    }
    
    /**
     * Load image and return result via callback
     * @param callback Callback for result handling
     */
    fun into(callback: ImageCallback) {
        val request = ImageRequest.Builder(url)
            .apply { placeholder?.let { placeholder(it) } }
            .apply { errorImage?.let { error(it) } }
            .resize(width, height)
            .callback(callback)
            .build()
        ImageLoaderFactory.getInstance().load(request)
    }
    
    /**
     * Enable center crop scaling
     * @return This builder for chaining
     */
    fun centerCrop() = ImageRequestBuilderWithScaling(url, placeholder, errorImage, true, false, width, height)
    
    /**
     * Enable center inside scaling
     * @return This builder for chaining
     */
    fun centerInside() = ImageRequestBuilderWithScaling(url, placeholder, errorImage, false, true, width, height)
}

/**
 * Builder class with scaling options
 */
class ImageRequestBuilderWithScaling(
    private val url: String,
    private val placeholder: Int?,
    private val errorImage: Int?,
    private val centerCrop: Boolean,
    private val centerInside: Boolean,
    private val width: Int? = null,
    private val height: Int? = null
) {
    
    /**
     * Load image into ImageView
     * @param imageView The target ImageView
     * @param callback Optional callback for result handling
     */
    fun into(imageView: ImageView, callback: ImageCallback? = null) {
        val request = ImageRequest.Builder(url)
            .apply { placeholder?.let { placeholder(it) } }
            .apply { errorImage?.let { error(it) } }
            .apply { 
                if (width != null && height != null) {
                    resize(width, height)
                }
            }
            .apply { if (centerCrop) centerCrop() }
            .apply { if (centerInside) centerInside() }
            .into(imageView)
            .callback(callback as ImageCallback)
            .build()
        ImageLoaderFactory.getInstance().load(request)
    }
    
    /**
     * Load image and return result via callback
     * @param callback Callback for result handling
     */
    fun into(callback: ImageCallback) {
        val request = ImageRequest.Builder(url)
            .apply { placeholder?.let { placeholder(it) } }
            .apply { errorImage?.let { error(it) } }
            .apply { 
                if (width != null && height != null) {
                    resize(width, height)
                }
            }
            .apply { if (centerCrop) centerCrop() }
            .apply { if (centerInside) centerInside() }
            .callback(callback)
            .build()
        ImageLoaderFactory.getInstance().load(request)
    }
}
