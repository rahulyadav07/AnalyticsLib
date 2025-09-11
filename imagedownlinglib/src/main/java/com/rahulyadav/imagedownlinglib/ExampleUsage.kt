package com.rahulyadav.imagedownlinglib

import android.content.Context
import android.widget.ImageView
import com.rahulyadav.imagedownlinglib.core.ImageCallback
import com.rahulyadav.imagedownlinglib.core.ImageResult
import com.rahulyadav.imagedownlinglib.config.ImageLoaderConfig

/**
 * Example usage of the ImageLoader library.
 * This file demonstrates how to use the library in different scenarios.
 */
class ExampleUsage {
    
    /**
     * Example 1: Basic initialization and usage
     */
    fun basicExample(context: Context, imageView: ImageView) {
        // Initialize with default configuration
        ImageLoader.initialize(context)
        
        // Load image with basic usage
        ImageLoader.load("https://example.com/image.jpg")
            .into(imageView)
    }
    
    /**
     * Example 2: Advanced configuration
     */
    fun advancedConfigurationExample(context: Context) {
        // Create custom configuration
        val config = ImageLoaderConfig.Builder(context)
            .memoryCacheSize(100 * 1024 * 1024) // 100MB memory cache
            .diskCacheSize(500 * 1024 * 1024)   // 500MB disk cache
            .timeout(60) // 60 seconds timeout
            .enableLogging() // Enable logging
            .build()
        
        // Initialize with custom configuration
        ImageLoader.initialize(context, config)
    }
    
    /**
     * Example 3: Loading with callbacks
     */
    fun callbackExample(imageView: ImageView) {
        ImageLoader.load("https://example.com/image.jpg")
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_dialog_alert)
            .into(imageView) { result ->
                when (result) {
                    is ImageResult.Success -> {
                        // Image loaded successfully
                        println("Image loaded successfully")
                    }
                    is ImageResult.Error -> {
                        // Handle error
                        println("Error loading image: ${result.exception.message}")
                    }
                    is ImageResult.Loading -> {
                        // Handle loading state
                        println("Loading image...")
                    }
                }
            }
    }
    
    /**
     * Example 4: Loading with resizing and scaling
     */
    fun resizeExample(imageView: ImageView) {
        ImageLoader.load("https://example.com/image.jpg")
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_dialog_alert)
            .resize(200, 200)
            .centerCrop()
            .into(imageView)
    }
    
    /**
     * Example 5: Loading without ImageView (just getting the bitmap)
     */
    fun bitmapOnlyExample() {
        ImageLoader.load("https://example.com/image.jpg")
            .into(object : ImageCallback {
                override fun onResult(result: ImageResult) {
                    when (result) {
                        is ImageResult.Success -> {
                            val bitmap = result.bitmap
                            // Use the bitmap as needed
                            println("Got bitmap: ${bitmap.width}x${bitmap.height}")
                        }
                        is ImageResult.Error -> {
                            println("Error: ${result.exception.message}")
                        }
                        is ImageResult.Loading -> {
                            println("Loading...")
                        }
                    }
                }
            })
    }
    
    /**
     * Example 6: Cache management
     */
    suspend fun cacheManagementExample() {
        // Get cache statistics
        val stats = ImageLoader.getCacheStats()
        println("Memory cache: ${stats.memoryCacheSize}/${stats.memoryCacheMaxSize} bytes")
        println("Disk cache: ${stats.diskCacheSize}/${stats.diskCacheMaxSize} bytes")
        
        // Clear all caches
        ImageLoader.clearCache()
        println("All caches cleared")
    }
    
    /**
     * Example 7: Request cancellation
     */
    fun cancellationExample(imageView: ImageView) {
        val url = "https://example.com/large-image.jpg"
        
        // Start loading
        ImageLoader.load(url).into(imageView)
        
        // Cancel the request if needed
        ImageLoader.cancel(url)
        
        // Or cancel all requests
        ImageLoader.cancelAll()
    }
}
