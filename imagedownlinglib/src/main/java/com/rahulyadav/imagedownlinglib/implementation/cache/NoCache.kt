package com.rahulyadav.imagedownlinglib.implementation.cache

import android.graphics.Bitmap
import com.rahulyadav.imagedownlinglib.strategy.ImageCache

/**
 * No-op cache implementation.
 * Follows Null Object pattern - provides default behavior when caching is disabled.
 * Follows Single Responsibility Principle - only handles no-cache scenario.
 */
class NoCache : ImageCache {
    
    override suspend fun get(key: String): Bitmap? = null
    
    override suspend fun put(key: String, bitmap: Bitmap) {
        // No-op: do nothing
    }
    
    override suspend fun remove(key: String) {
        // No-op: do nothing
    }
    
    override suspend fun clear() {
        // No-op: do nothing
    }
    
    override suspend fun size(): Long = 0L
    
    override fun maxSize(): Long = 0L
}
