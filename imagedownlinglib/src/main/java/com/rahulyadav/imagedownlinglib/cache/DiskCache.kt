package com.rahulyadav.imagedownlinglib.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest

/**
 * Disk cache implementation using file system.
 * Follows Single Responsibility Principle - only handles disk caching.
 * Thread-safe implementation using coroutines.
 */
class DiskCache(
    private val context: Context,
    private val maxSize: Long,
    private val cacheDir: String = "image_cache"
) : ImageCache {
    
    private val cacheDirectory: File by lazy {
        File(context.cacheDir, cacheDir).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    override suspend fun get(key: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val file = getCacheFile(key)
            if (file.exists() && file.length() > 0) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun put(key: String, bitmap: Bitmap) = withContext(Dispatchers.IO) {
        try {
            val file = getCacheFile(key)
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            
            // Clean up old files if cache is too large
            cleanupIfNeeded()
        } catch (e: IOException) {
            // Log error or handle silently
        }
    }
    
    override suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        try {
            getCacheFile(key).delete()
        } catch (e: Exception) {
            // Log error or handle silently
        }
        Unit
    }
    
    override suspend fun clear() = withContext(Dispatchers.IO) {
        try {
            cacheDirectory.listFiles()?.forEach { it.delete() }
        } catch (e: Exception) {
            // Log error or handle silently
        }
        Unit
    }
    
    override suspend fun size(): Long = withContext(Dispatchers.IO) {
        try {
            cacheDirectory.listFiles()?.sumOf { it.length() } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
    
    override fun maxSize(): Long = maxSize
    
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
            
            var sizeToRemove = currentSize - (maxSize * 0.8).toLong() // Remove 20% of max size
            var removedSize = 0L
            
            for (file in files) {
                if (removedSize >= sizeToRemove) break
                removedSize += file.length()
                file.delete()
            }
        }
    }
}
