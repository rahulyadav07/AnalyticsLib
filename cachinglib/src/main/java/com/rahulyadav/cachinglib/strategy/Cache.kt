package com.rahulyadav.cachinglib.strategy

/**
 * Interface for cache operations.
 * Follows Strategy pattern - different cache implementations can be used interchangeably.
 * Follows Single Responsibility Principle - only handles caching operations.
 */
interface Cache<T> {
    
    /**
     * Get value from cache
     * @param key The cache key
     * @return Value if found in cache, null otherwise
     */
    suspend fun get(key: String): T?
    
    /**
     * Put value into cache
     * @param key The cache key
     * @param value The value to cache
     */
    suspend fun put(key: String, value: T)
    
    /**
     * Remove value from cache
     * @param key The cache key to remove
     */
    suspend fun remove(key: String)
    
    /**
     * Clear all cached items
     */
    suspend fun clear()
    
    /**
     * Check if key exists in cache
     * @param key The cache key
     * @return true if key exists, false otherwise
     */
    suspend fun contains(key: String): Boolean
    
    /**
     * Get current cache size
     * @return Number of items in cache
     */
    suspend fun size(): Int
    
    /**
     * Get maximum cache size
     * @return Maximum number of items cache can hold
     */
    fun maxSize(): Int
}
