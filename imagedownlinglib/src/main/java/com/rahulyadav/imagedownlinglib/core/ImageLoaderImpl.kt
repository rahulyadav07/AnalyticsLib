package com.rahulyadav.imagedownlinglib.core

import android.graphics.Bitmap
import android.widget.ImageView
import com.rahulyadav.imagedownlinglib.cache.ImageCache
import com.rahulyadav.imagedownlinglib.network.NetworkClient
import com.rahulyadav.imagedownlinglib.decoder.ImageDecoder
import com.rahulyadav.imagedownlinglib.config.ImageLoaderConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Main implementation of ImageLoader.
 * Follows Facade pattern - provides simple interface to complex subsystems.
 * Follows Single Responsibility Principle - orchestrates image loading operations.
 * Thread-safe implementation using coroutines and proper lifecycle management.
 */
class ImageLoaderImpl(
    private val config: ImageLoaderConfig
) : ImageLoader {
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val activeRequests = mutableMapOf<String, Job>()
    private val _cacheStats = MutableSharedFlow<CacheStats>()
    
    override fun load(url: String, imageView: ImageView, callback: ImageCallback?) {
        val request = ImageRequest.Builder(url)
            .into(imageView)
            .callback(callback)
            .build()
        load(request)
    }
    
    override fun load(url: String, callback: ImageCallback) {
        val request = ImageRequest.Builder(url)
            .callback(callback)
            .build()
        load(request)
    }
    
    override fun load(request: ImageRequest) {
        // Cancel any existing request for this URL
        cancel(request.url)
        
        // Set placeholder if provided
        request.imageView?.setImageResource(request.placeholder ?: 0)
        
        // Start loading
        val job = scope.launch {
            try {
                val result = loadImage(request)
                
                // Update UI on main thread
                withContext(Dispatchers.Main) {
                    when (result) {
                        is ImageResult.Success -> {
                            request.imageView?.setImageBitmap(result.bitmap)
                            request.callback?.onResult(result)
                        }
                        is ImageResult.Error -> {
                            request.imageView?.setImageResource(request.errorImage ?: 0)
                            request.callback?.onResult(result)
                        }
                        is ImageResult.Loading -> {
                            // Handle loading state if needed
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorResult = ImageResult.Error(e)
                    request.imageView?.setImageResource(request.errorImage ?: 0)
                    request.callback?.onResult(errorResult)
                }
            } finally {
                activeRequests.remove(request.url)
            }
        }
        
        activeRequests[request.url] = job
    }
    
    override fun cancel(url: String) {
        activeRequests[url]?.cancel()
        activeRequests.remove(url)
    }
    
    override fun cancelAll() {
        activeRequests.values.forEach { it.cancel() }
        activeRequests.clear()
    }
    
    override suspend fun clearCache() {
        withContext(Dispatchers.IO) {
            config.memoryCache.clear()
            config.diskCache.clear()
        }
    }
    
    override suspend fun getCacheStats(): CacheStats {
        return withContext(Dispatchers.IO) {
            CacheStats(
                memoryCacheSize = config.memoryCache.size(),
                memoryCacheMaxSize = config.memoryCache.maxSize(),
                diskCacheSize = config.diskCache.size(),
                diskCacheMaxSize = config.diskCache.maxSize()
            )
        }
    }
    
    private suspend fun loadImage(request: ImageRequest): ImageResult {
        return withContext(Dispatchers.IO) {
            try {
                // Check memory cache first
                val memoryCached = config.memoryCache.get(request.url)
                if (memoryCached != null) {
                    if (config.enableLogging) {
                        println("ImageLoader: Loaded from memory cache: ${request.url}")
                    }
                    return@withContext ImageResult.Success(memoryCached)
                }
                
                // Check disk cache
                val diskCached = config.diskCache.get(request.url)
                if (diskCached != null) {
                    if (config.enableLogging) {
                        println("ImageLoader: Loaded from disk cache: ${request.url}")
                    }
                    // Store in memory cache for future use
                    config.memoryCache.put(request.url, diskCached)
                    return@withContext ImageResult.Success(diskCached)
                }
                
                // Download from network
                if (config.enableLogging) {
                    println("ImageLoader: Downloading from network: ${request.url}")
                }
                
                val imageData = config.networkClient.download(request.url)
                
                // Decode image
                val bitmap = config.imageDecoder.decode(
                    data = imageData,
                    targetWidth = request.targetWidth,
                    targetHeight = request.targetHeight,
                    centerCrop = request.centerCrop,
                    centerInside = request.centerInside
                )
                
                // Store in caches
                config.memoryCache.put(request.url, bitmap)
                config.diskCache.put(request.url, bitmap)
                
                if (config.enableLogging) {
                    println("ImageLoader: Successfully loaded and cached: ${request.url}")
                }
                
                ImageResult.Success(bitmap)
            } catch (e: Exception) {
                if (config.enableLogging) {
                    println("ImageLoader: Error loading image: ${request.url}, Error: ${e.message}")
                }
                ImageResult.Error(e)
            }
        }
    }
    
    /**
     * Clean up resources
     */
    fun destroy() {
        cancelAll()
        scope.cancel()
    }
}
