package com.rahulyadav.cachinglib.core

/**
 * Base exception class for all cache-related errors.
 * Follows the exception hierarchy pattern for better error handling.
 */
sealed class CacheException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    
    /**
     * Serialization-related errors
     */
    class SerializationException(message: String, cause: Throwable? = null) : CacheException(message, cause)
    
    /**
     * Deserialization-related errors
     */
    class DeserializationException(message: String, cause: Throwable? = null) : CacheException(message, cause)
    
    /**
     * File system-related errors
     */
    class FileSystemException(message: String, cause: Throwable? = null) : CacheException(message, cause)
    
    /**
     * Memory-related errors
     */
    class MemoryException(message: String, cause: Throwable? = null) : CacheException(message, cause)
    
    /**
     * Configuration-related errors
     */
    class ConfigurationException(message: String, cause: Throwable? = null) : CacheException(message, cause)
}
