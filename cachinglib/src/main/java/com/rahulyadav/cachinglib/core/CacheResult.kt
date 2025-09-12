package com.rahulyadav.cachinglib.core

/**
 * Represents the result of a cache operation.
 * Uses sealed class for type safety and exhaustive when expressions.
 */
sealed class CacheResult<T> {
    /**
     * Success result containing the cached value
     */
    data class Success<T>(val value: T) : CacheResult<T>()
    
    /**
     * Cache miss - value not found
     */
    class Miss<T> : CacheResult<T>()
    
    /**
     * Error result containing the exception that occurred
     */
    data class Error<T>(val exception: Throwable) : CacheResult<T>()
}
