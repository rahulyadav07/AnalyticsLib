package com.rahulyadav.imagedownlinglib.strategy

import android.graphics.Bitmap

/**
 * Interface for image decoding operations.
 * Follows Strategy pattern - different decoding implementations can be used.
 * Follows Single Responsibility Principle - only handles image decoding.
 */
interface ImageDecoder {
    
    /**
     * Decode image data into a Bitmap
     * @param data The image data as ByteArray
     * @param targetWidth Optional target width for resizing
     * @param targetHeight Optional target height for resizing
     * @param centerCrop Whether to center crop the image
     * @param centerInside Whether to center inside the image
     * @return Decoded Bitmap
     * @throws DecodeException if decoding fails
     */
    suspend fun decode(
        data: ByteArray,
        targetWidth: Int? = null,
        targetHeight: Int? = null,
        centerCrop: Boolean = false,
        centerInside: Boolean = false
    ): Bitmap
    
    /**
     * Check if the decoder can handle the given image format
     * @param data The image data to check
     * @return true if this decoder can handle the format
     */
    fun canDecode(data: ByteArray): Boolean
}

/**
 * Custom exception for decoding-related errors
 */
class DecodeException(message: String, cause: Throwable? = null) : Exception(message, cause)
