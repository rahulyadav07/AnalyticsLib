package com.rahulyadav.cachinglib.strategy

/**
 * Interface for serialization operations.
 * Follows Strategy pattern - different serialization implementations can be used.
 * Follows Single Responsibility Principle - only handles serialization.
 */
interface Serializer<T> {
    
    /**
     * Serialize object to string
     * @param obj The object to serialize
     * @return Serialized string
     * @throws SerializationException if serialization fails
     */
    fun serialize(obj: T): String
    
    /**
     * Deserialize string to object
     * @param data The serialized string
     * @param clazz The class type to deserialize to
     * @return Deserialized object
     * @throws DeserializationException if deserialization fails
     */
    fun <R : T> deserialize(data: String, clazz: Class<R>): R
    
    /**
     * Check if this serializer can handle the given type
     * @param clazz The class type to check
     * @return true if this serializer can handle the type
     */
    fun canHandle(clazz: Class<*>): Boolean
}
