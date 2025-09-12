package com.rahulyadav.cachinglib.implementation

import android.content.Context
import com.rahulyadav.cachinglib.strategy.Cache
import com.rahulyadav.cachinglib.strategy.Serializer
import com.rahulyadav.cachinglib.core.CacheException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.security.MessageDigest

/**
 * Disk cache implementation using file system.
 * Follows Single Responsibility Principle - only handles disk caching.
 * Thread-safe implementation using coroutines.
 */
class DiskCache<T>(
    private val context: Context,
    private val serializer: Serializer<T>,
    private val maxSize: Int = 1000,
    private val cacheDir: String = "cache"
) : Cache<T> {
    
    private val cacheDirectory: File by lazy {
        File(context.cacheDir, cacheDir).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    override suspend fun get(key: String): T? = withContext(Dispatchers.IO) {
        try {
            val file = getCacheFile(key)
            if (file.exists() && file.length() > 0) {
                val data = file.readText()
                serializer.deserialize(data, Any::class.java as Class<T>)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun put(key: String, value: T) = withContext(Dispatchers.IO) {
        try {
            val file = getCacheFile(key)
            val data = serializer.serialize(value)
            file.writeText(data)
            
            // Clean up old files if cache is too large
            cleanupIfNeeded()
        } catch (e: IOException) {
            throw CacheException.FileSystemException("Failed to write cache file: ${e.message}", e)
        } catch (e: Exception) {
            throw CacheException.SerializationException("Failed to serialize value: ${e.message}", e)
        }
    }
    
    override suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        try {
            getCacheFile(key).delete()
        } catch (e: Exception) {
            // Log error or handle silently
        }
    }
    
    override suspend fun clear() = withContext(Dispatchers.IO) {
        try {
            cacheDirectory.listFiles()?.forEach { it.delete() }
        } catch (e: Exception) {
            // Log error or handle silently
        }
    }
    
    override suspend fun contains(key: String): Boolean = withContext(Dispatchers.IO) {
        try {
            getCacheFile(key).exists()
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun size(): Int = withContext(Dispatchers.IO) {
        try {
            cacheDirectory.listFiles()?.size ?: 0
        } catch (e: Exception) {
            0
        }
    }
    
    override fun maxSize(): Int = maxSize
    
    private fun getCacheFile(key: String): File {
        val hash = hashKey(key)
        return File(cacheDirectory, hash)
    }
    
    private fun hashKey(key: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val hashBytes = digest.digest(key.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    private suspend fun cleanupIfNeeded() = withContext(Dispatchers.IO) {
        val currentSize = size()
        if (currentSize > maxSize) {
            val files = cacheDirectory.listFiles()?.sortedBy { it.lastModified() } ?: return@withContext
            
            val filesToRemove = currentSize - (maxSize * 0.8).toInt() // Remove 20% of max size
            
            for (i in 0 until minOf(filesToRemove, files.size)) {
                files[i].delete()
            }
        }
    }
}
