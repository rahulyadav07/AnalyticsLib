package com.rahulyadav.imagedownlinglib.cache

import android.graphics.Bitmap

/**
 * Interface for image caching strategies.
 * Follows Strategy pattern - different implementations can be used interchangeably.
 * Follows Single Responsibility Principle - only handles caching operations.
 */
interface ImageCache {
    
    /**
     * Get bitmap from cache
     * @param key The cache key (usually URL)
     * @return Bitmap if found in cache, null otherwise
     */
    suspend fun get(key: String): Bitmap?
    
    /**
     * Put bitmap into cache
     * @param key The cache key (usually URL)
     * @param bitmap The bitmap to cache
     */
    suspend fun put(key: String, bitmap: Bitmap)
    
    /**
     * Remove bitmap from cache
     * @param key The cache key to remove
     */
    suspend fun remove(key: String)
    
    /**
     * Clear all cached items
     */
    suspend fun clear()
    
    /**
     * Get current cache size in bytes
     * @return Current cache size
     */
    suspend fun size(): Long
    
    /**
     * Get maximum cache size in bytes
     * @return Maximum cache size
     */
    fun maxSize(): Long
}
