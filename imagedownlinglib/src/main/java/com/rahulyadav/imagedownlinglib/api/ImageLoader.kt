package com.rahulyadav.imagedownlinglib.api

import android.content.Context
import android.widget.ImageView
import com.rahulyadav.imagedownlinglib.core.ImageLoaderImpl
import com.rahulyadav.imagedownlinglib.core.ImageRequest
import com.rahulyadav.imagedownlinglib.config.ImageLoaderConfig

/**
 * Simple Image Loading Library.
 * Just load, placeholder, and resize - that's it!
 */
object ImageLoader {
    
    private var instance: ImageLoaderImpl? = null
    
    /**
     * Initialize once in your Application class
     */
    fun initialize(context: Context) {
        if (instance == null) {
            val config = ImageLoaderConfig.Builder(context)
                .memoryCacheSize(50 * 1024 * 1024) // 50MB
                .diskCacheSize(200 * 1024 * 1024)  // 200MB
                .timeout(30) // 30 seconds
                .build()
            instance = ImageLoaderImpl(config)
        }
    }
    
    /**
     * Load image from URL
     */
    fun load(url: String): ImageRequestBuilder {
        return ImageRequestBuilder(url)
    }
}

/**
 * Simple builder for image requests
 */
class ImageRequestBuilder(private val url: String) {
    private var placeholder: Int? = null
    private var targetWidth: Int? = null
    private var targetHeight: Int? = null
    
    /**
     * Set placeholder image
     */
    fun placeholder(resourceId: Int) = apply {
        this.placeholder = resourceId
    }
    
    /**
     * Resize image to specific dimensions
     */
    fun resize(width: Int, height: Int) = apply {
        this.targetWidth = width
        this.targetHeight = height
    }
    
    /**
     * Load image into ImageView
     */
    fun into(imageView: ImageView) {
        val request = ImageRequest.Builder(url)
            .apply { placeholder?.let { placeholder(it) } }
            .apply { 
                if (targetWidth != null && targetHeight != null) {
                    resize(targetWidth!!, targetHeight!!)
                }
            }
            .into(imageView)
            .build()
        
        ImageLoader.instance?.load(request) ?: throw IllegalStateException("ImageLoader not initialized. Call ImageLoader.initialize(context) first.")
    }
}