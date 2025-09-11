package com.rahulyadav.imagedownlinglib.core

import android.graphics.Bitmap

/**
 * Represents the result of an image loading operation.
 * Uses sealed class for type safety and exhaustive when expressions.
 */
sealed class ImageResult {
    /**
     * Success result containing the loaded bitmap
     */
    data class Success(val bitmap: Bitmap) : ImageResult()
    
    /**
     * Error result containing the exception that occurred
     */
    data class Error(val exception: Throwable) : ImageResult()
    
    /**
     * Loading state (optional, for progress tracking)
     */
    object Loading : ImageResult()
}
