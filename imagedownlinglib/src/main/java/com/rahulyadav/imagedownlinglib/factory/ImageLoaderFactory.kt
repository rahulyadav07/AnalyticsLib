package com.rahulyadav.imagedownlinglib.factory

import android.content.Context
import com.rahulyadav.imagedownlinglib.core.ImageLoader
import com.rahulyadav.imagedownlinglib.core.ImageLoaderImpl
import com.rahulyadav.imagedownlinglib.config.ImageLoaderConfig

/**
 * Factory class for creating ImageLoader instances.
 * Follows Factory pattern for object creation.
 * Follows Singleton pattern for global access.
 */
object ImageLoaderFactory {
    
    private var instance: ImageLoader? = null
    
    /**
     * Initialize the global ImageLoader instance
     * @param context Application context
     * @param config Configuration for the ImageLoader
     */
    fun initialize(context: Context, config: ImageLoaderConfig) {
        instance = ImageLoaderImpl(config)
    }
    
    /**
     * Initialize with default configuration
     * @param context Application context
     */
    fun initialize(context: Context) {
        val config = ImageLoaderConfig.Builder(context)
            .memoryCacheSize(50 * 1024 * 1024) // 50MB
            .diskCacheSize(200 * 1024 * 1024)  // 200MB
            .timeout(30) // 30 seconds
            .build()
        initialize(context, config)
    }
    
    /**
     * Get the global ImageLoader instance
     * @return ImageLoader instance
     * @throws IllegalStateException if not initialized
     */
    fun getInstance(): ImageLoader {
        return instance ?: throw IllegalStateException(
            "ImageLoader not initialized. Call ImageLoaderFactory.initialize() first."
        )
    }
    
    /**
     * Check if ImageLoader is initialized
     * @return true if initialized, false otherwise
     */
    fun isInitialized(): Boolean {
        return instance != null
    }
    
    /**
     * Destroy the global instance (useful for testing)
     */
    fun destroy() {
        (instance as? ImageLoaderImpl)?.destroy()
        instance = null
    }
}
