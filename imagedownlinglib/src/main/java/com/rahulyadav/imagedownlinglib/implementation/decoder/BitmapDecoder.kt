package com.rahulyadav.imagedownlinglib.implementation.decoder

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.rahulyadav.imagedownlinglib.strategy.ImageDecoder

/**
 * Bitmap decoder implementation using Android's BitmapFactory.
 * Follows Single Responsibility Principle - only handles image decoding.
 * Thread-safe implementation using coroutines.
 */
class BitmapDecoder : ImageDecoder {
    
    override suspend fun decode(
        data: ByteArray,
        targetWidth: Int?,
        targetHeight: Int?,
        centerCrop: Boolean,
        centerInside: Boolean
    ): Bitmap = withContext(Dispatchers.IO) {
        try {
            // First, decode with just bounds to get original dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(data, 0, data.size, options)
            
            val originalWidth = options.outWidth
            val originalHeight = options.outHeight
            
            if (originalWidth <= 0 || originalHeight <= 0) {
                throw DecodeException("Invalid image dimensions: ${originalWidth}x${originalHeight}")
            }
            
            // Calculate sample size for memory efficiency
            val sampleSize = calculateSampleSize(
                originalWidth, originalHeight,
                targetWidth, targetHeight
            )
            
            // Decode the actual bitmap
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inJustDecodeBounds = false
            }
            
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size, decodeOptions)
                ?: throw DecodeException("Failed to decode bitmap")
            
            // Apply scaling if needed
            if (targetWidth != null && targetHeight != null) {
                scaleBitmap(bitmap, targetWidth, targetHeight, centerCrop, centerInside)
            } else {
                bitmap
            }
        } catch (e: Exception) {
            if (e is DecodeException) throw e
            throw DecodeException("Failed to decode image: ${e.message}", e)
        }
    }
    
    override fun canDecode(data: ByteArray): Boolean {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(data, 0, data.size, options)
            options.outWidth > 0 && options.outHeight > 0
        } catch (e: Exception) {
            false
        }
    }
    
    private fun calculateSampleSize(
        originalWidth: Int,
        originalHeight: Int,
        targetWidth: Int?,
        targetHeight: Int?
    ): Int {
        if (targetWidth == null || targetHeight == null) {
            return 1
        }
        
        var sampleSize = 1
        
        if (originalHeight > targetHeight || originalWidth > targetWidth) {
            val halfHeight = originalHeight / 2
            val halfWidth = originalWidth / 2
            
            while ((halfHeight / sampleSize) >= targetHeight && (halfWidth / sampleSize) >= targetWidth) {
                sampleSize *= 2
            }
        }
        
        return sampleSize
    }
    
    private fun scaleBitmap(
        originalBitmap: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        centerCrop: Boolean,
        centerInside: Boolean
    ): Bitmap {
        val originalWidth = originalBitmap.width
        val originalHeight = originalBitmap.height
        
        val scaleX = targetWidth.toFloat() / originalWidth
        val scaleY = targetHeight.toFloat() / originalHeight
        
        val scale = when {
            centerCrop -> maxOf(scaleX, scaleY)
            centerInside -> minOf(scaleX, scaleY)
            else -> minOf(scaleX, scaleY)
        }
        
        val scaledWidth = (originalWidth * scale).toInt()
        val scaledHeight = (originalHeight * scale).toInt()
        
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true)
        
        // If center crop is enabled and we need to crop
        if (centerCrop && (scaledWidth > targetWidth || scaledHeight > targetHeight)) {
            val startX = (scaledWidth - targetWidth) / 2
            val startY = (scaledHeight - targetHeight) / 2
            
            val croppedBitmap = Bitmap.createBitmap(
                scaledBitmap,
                startX,
                startY,
                targetWidth,
                targetHeight
            )
            
            if (scaledBitmap != originalBitmap) {
                scaledBitmap.recycle()
            }
            
            return croppedBitmap
        }
        
        return scaledBitmap
    }
}
