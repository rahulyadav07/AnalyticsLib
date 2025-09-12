package com.rahulyadav.cachinglib.api

import android.content.Context
import kotlinx.coroutines.runBlocking

/**
 * Example usage of the Cache library.
 * Demonstrates the simple API and common use cases.
 */
class ExampleUsage {
    
    fun basicUsage(context: Context) {
        // Initialize once in Application class
        Cache.initialize(context)
        
        runBlocking {
            // Store data
            Cache.put("user_name", "John Doe")
            Cache.put("user_age", 25)
            Cache.put("user_email", "john@example.com")
            
            // Retrieve data
            val name: String? = Cache.get("user_name")
            val age: Int? = Cache.get("user_age")
            val email: String? = Cache.get("user_email")
            
            println("Name: $name, Age: $age, Email: $email")
        }
    }
    
    fun objectCaching(context: Context) {
        Cache.initialize(context)
        
        data class User(val id: Int, val name: String, val email: String)
        
        runBlocking {
            val user = User(1, "Jane Doe", "jane@example.com")
            
            // Store object
            Cache.put("user_1", user)
            
            // Retrieve object
            val cachedUser: User? = Cache.get("user_1")
            println("Cached user: $cachedUser")
        }
    }
    
    fun cacheManagement(context: Context) {
        Cache.initialize(context)
        
        runBlocking {
            // Check if key exists
            val exists = Cache.contains("user_name")
            println("Key exists: $exists")
            
            // Remove specific key
            Cache.remove("user_name")
            
            // Get cache statistics
            val stats = Cache.getStats()
            stats?.let {
                println("Memory cache: ${it.memoryCacheSize}/${it.memoryCacheMaxSize}")
                println("Disk cache: ${it.diskCacheSize}/${it.diskCacheMaxSize}")
            }
            
            // Clear all caches
            Cache.clear()
        }
    }
    
    fun errorHandling(context: Context) {
        Cache.initialize(context)
        
        runBlocking {
            try {
                // Try to get non-existent key
                val value: String? = Cache.get("non_existent_key")
                if (value == null) {
                    println("Key not found in cache")
                }
                
                // Store and retrieve
                val success = Cache.put("test_key", "test_value")
                if (success) {
                    println("Successfully stored value")
                } else {
                    println("Failed to store value")
                }
                
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }
}
