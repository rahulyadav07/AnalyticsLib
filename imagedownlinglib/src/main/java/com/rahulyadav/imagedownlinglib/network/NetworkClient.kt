package com.rahulyadav.imagedownlinglib.network

/**
 * Interface for network operations.
 * Follows Strategy pattern - different network implementations can be used.
 * Follows Single Responsibility Principle - only handles network operations.
 */
interface NetworkClient {
    
    /**
     * Download data from the given URL
     * @param url The URL to download from
     * @return ByteArray of the downloaded data
     * @throws NetworkException if download fails
     */
    suspend fun download(url: String): ByteArray
    
    /**
     * Check if the URL is reachable
     * @param url The URL to check
     * @return true if reachable, false otherwise
     */
    suspend fun isReachable(url: String): Boolean
}

/**
 * Custom exception for network-related errors
 */
class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
