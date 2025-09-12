package com.rahulyadav.imagedownlinglib.api

import android.content.Context
import android.widget.ImageView

/**
 * Simple usage examples for the ImageLoader library.
 */
class ExampleUsage {
    
    /**
     * Example 1: Basic initialization and usage
     */
    fun basicExample(context: Context, imageView: ImageView) {
        // Initialize once in Application class
        ImageLoader.initialize(context)
        
        // Simple image loading
        ImageLoader.load("https://example.com/image.jpg")
            .into(imageView)
    }
    
    /**
     * Example 2: With placeholder
     */
    fun placeholderExample(imageView: ImageView) {
        ImageLoader.load("https://example.com/image.jpg")
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(imageView)
    }
    
    /**
     * Example 3: With resizing
     */
    fun resizeExample(imageView: ImageView) {
        ImageLoader.load("https://example.com/image.jpg")
            .placeholder(android.R.drawable.ic_menu_gallery)
            .resize(200, 200)
            .into(imageView)
    }
    
    /**
     * Example 4: All together
     */
    fun completeExample(imageView: ImageView) {
        ImageLoader.load("https://example.com/image.jpg")
            .placeholder(android.R.drawable.ic_menu_gallery)
            .resize(300, 300)
            .into(imageView)
    }
}