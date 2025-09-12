package com.rahulyadav.cachinglib.implementation

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rahulyadav.cachinglib.strategy.Serializer
import com.rahulyadav.cachinglib.core.CacheException
import java.lang.reflect.Type

/**
 * Gson-based serializer implementation.
 * Follows Single Responsibility Principle - only handles JSON serialization.
 */
class GsonSerializer<T> : Serializer<T> {
    
    private val gson = Gson()
    
    override fun serialize(obj: T): String {
        return try {
            gson.toJson(obj)
        } catch (e: Exception) {
            throw CacheException.SerializationException("Failed to serialize object: ${e.message}", e)
        }
    }
    
    override fun <R : T> deserialize(data: String, clazz: Class<R>): R {
        return try {
            gson.fromJson(data, clazz)
        } catch (e: Exception) {
            throw CacheException.DeserializationException("Failed to deserialize data: ${e.message}", e)
        }
    }
    
    override fun canHandle(clazz: Class<*>): Boolean {
        return try {
            // Try to serialize a dummy object to check if Gson can handle the type
            gson.toJson(createDummyInstance(clazz))
            true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun createDummyInstance(clazz: Class<*>): Any? {
        return try {
            clazz.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            null
        }
    }
}
