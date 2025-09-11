package com.rahulyadav.imagedownlinglib.utils

import com.rahulyadav.imagedownlinglib.core.ImageLoaderException
import java.net.URL

/**
 * Utility class for URL validation.
 * Follows Single Responsibility Principle - only handles URL validation.
 */
object UrlValidator {
    
    /**
     * Validate if the given string is a valid URL
     * @param url The URL string to validate
     * @return true if valid, false otherwise
     */
    fun isValid(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        
        return try {
            URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Validate URL and throw exception if invalid
     * @param url The URL string to validate
     * @throws ImageLoaderException.InvalidUrlException if URL is invalid
     */
    fun validate(url: String?) {
        if (!isValid(url)) {
            throw ImageLoaderException.InvalidUrlException("Invalid URL: $url")
        }
    }
    
    /**
     * Check if URL is an image URL based on common image extensions
     * @param url The URL to check
     * @return true if likely an image URL, false otherwise
     */
    fun isImageUrl(url: String?): Boolean {
        if (!isValid(url)) return false
        
        val imageExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg")
        val lowerUrl = url!!.lowercase()
        
        return imageExtensions.any { lowerUrl.contains(it) }
    }
}
