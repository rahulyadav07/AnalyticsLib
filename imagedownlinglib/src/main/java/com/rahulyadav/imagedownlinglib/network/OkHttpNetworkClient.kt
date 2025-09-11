package com.rahulyadav.imagedownlinglib.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * OkHttp implementation of NetworkClient.
 * Follows Single Responsibility Principle - only handles network operations.
 * Thread-safe implementation using coroutines.
 */
class OkHttpNetworkClient(
    private val timeoutSeconds: Long = 30
) : NetworkClient {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .build()
    
    override suspend fun download(url: String): ByteArray = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .build()
            
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw NetworkException("HTTP ${response.code}: ${response.message}")
                }
                
                response.body?.bytes() ?: throw NetworkException("Empty response body")
            }
        } catch (e: IOException) {
            throw NetworkException("Network error: ${e.message}", e)
        } catch (e: Exception) {
            throw NetworkException("Unexpected error: ${e.message}", e)
        }
    }
    
    override suspend fun isReachable(url: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .head() // Use HEAD request to check reachability without downloading
                .build()
            
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }
}
