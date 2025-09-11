package com.rahulyadav.imagedownlinglib.core

/**
 * Base exception class for all ImageLoader-related errors.
 * Follows the exception hierarchy pattern for better error handling.
 */
sealed class ImageLoaderException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    
    /**
     * Network-related errors
     */
    class NetworkException(message: String, cause: Throwable? = null) : ImageLoaderException(message, cause)
    
    /**
     * Decoding-related errors
     */
    class DecodeException(message: String, cause: Throwable? = null) : ImageLoaderException(message, cause)
    
    /**
     * Cache-related errors
     */
    class CacheException(message: String, cause: Throwable? = null) : ImageLoaderException(message, cause)
    
    /**
     * Configuration-related errors
     */
    class ConfigurationException(message: String, cause: Throwable? = null) : ImageLoaderException(message, cause)
    
    /**
     * Invalid URL errors
     */
    class InvalidUrlException(message: String, cause: Throwable? = null) : ImageLoaderException(message, cause)
    
    /**
     * Timeout errors
     */
    class TimeoutException(message: String, cause: Throwable? = null) : ImageLoaderException(message, cause)
    
    /**
     * Out of memory errors
     */
    class OutOfMemoryException(message: String, cause: Throwable? = null) : ImageLoaderException(message, cause)
}
