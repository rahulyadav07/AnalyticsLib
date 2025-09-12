package com.rahulyadav.cachinglib.utils

/**
 * Utility for validating cache keys.
 * Follows Single Responsibility Principle - only handles key validation.
 */
object KeyValidator {
    
    private const val MAX_KEY_LENGTH = 250
    private const val MIN_KEY_LENGTH = 1
    
    /**
     * Validate cache key
     * @param key The key to validate
     * @return true if key is valid, false otherwise
     */
    fun isValid(key: String?): Boolean {
        if (key == null) return false
        if (key.length < MIN_KEY_LENGTH) return false
        if (key.length > MAX_KEY_LENGTH) return false
        if (key.trim().isEmpty()) return false
        
        // Check for invalid characters
        val invalidChars = charArrayOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
        return !key.any { it in invalidChars }
    }
    
    /**
     * Sanitize cache key by removing invalid characters
     * @param key The key to sanitize
     * @return Sanitized key
     */
    fun sanitize(key: String): String {
        val invalidChars = charArrayOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
        return key.filter { it !in invalidChars }.trim()
    }
}
