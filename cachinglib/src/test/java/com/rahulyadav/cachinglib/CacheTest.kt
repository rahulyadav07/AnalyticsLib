package com.rahulyadav.cachinglib

import com.rahulyadav.cachinglib.api.Cache
import com.rahulyadav.cachinglib.core.CacheResult
import com.rahulyadav.cachinglib.implementation.MemoryCache
import com.rahulyadav.cachinglib.implementation.NoCache
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

/**
 * Example unit tests for the Cache library.
 * Demonstrates testing capabilities and usage patterns.
 */
class CacheTest {
    
    @Test
    fun testMemoryCache() = runTest {
        val cache = MemoryCache<String>(10)
        
        // Test put and get
        cache.put("key1", "value1")
        val value = cache.get("key1")
        assertEquals("value1", value)
        
        // Test contains
        assertTrue(cache.contains("key1"))
        assertFalse(cache.contains("key2"))
        
        // Test remove
        cache.remove("key1")
        assertNull(cache.get("key1"))
        assertFalse(cache.contains("key1"))
        
        // Test clear
        cache.put("key2", "value2")
        cache.clear()
        assertNull(cache.get("key2"))
        assertEquals(0, cache.size())
    }
    
    @Test
    fun testNoCache() = runTest {
        val cache = NoCache<String>()
        
        // Test that NoCache always returns null
        cache.put("key1", "value1")
        assertNull(cache.get("key1"))
        assertFalse(cache.contains("key1"))
        assertEquals(0, cache.size())
        assertEquals(0, cache.maxSize())
        
        // Test that operations don't throw exceptions
        cache.remove("key1")
        cache.clear()
    }
    
    @Test
    fun testCacheResult() {
        // Test Success result
        val success = CacheResult.Success("test")
        assertTrue(success is CacheResult.Success)
        assertEquals("test", success.value)
        
        // Test Miss result
        val miss = CacheResult.Miss<String>()
        assertTrue(miss is CacheResult.Miss)
        
        // Test Error result
        val error = CacheResult.Error<String>(Exception("test error"))
        assertTrue(error is CacheResult.Error)
        assertEquals("test error", error.exception.message)
    }
    
    @Test
    fun testCacheStats() {
        val stats = com.rahulyadav.cachinglib.core.CacheStats(
            memoryCacheSize = 5,
            memoryCacheMaxSize = 100,
            diskCacheSize = 10,
            diskCacheMaxSize = 1000
        )
        
        assertEquals(5, stats.memoryCacheSize)
        assertEquals(100, stats.memoryCacheMaxSize)
        assertEquals(10, stats.diskCacheSize)
        assertEquals(1000, stats.diskCacheMaxSize)
    }
}
