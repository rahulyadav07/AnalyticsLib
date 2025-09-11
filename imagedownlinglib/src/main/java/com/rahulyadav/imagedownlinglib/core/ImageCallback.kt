package com.rahulyadav.imagedownlinglib.core

/**
 * Callback interface for image loading operations.
 * Follows the Observer pattern for async operations.
 */
interface ImageCallback {
    /**
     * Called when the image loading operation completes
     * @param result The result of the image loading operation
     */
    fun onResult(result: ImageResult)
}
