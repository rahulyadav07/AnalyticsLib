package com.rahulyadav.imagedownlinglib.core

import android.graphics.Bitmap
import android.widget.ImageView

/**
 * Represents an image loading request.
 * Uses Builder pattern for fluent API and easy configuration.
 */
data class ImageRequest(
    val url: String,
    val placeholder: Int? = null,
    val errorImage: Int? = null,
    val targetWidth: Int? = null,
    val targetHeight: Int? = null,
    val centerCrop: Boolean = false,
    val centerInside: Boolean = false,
    val callback: ImageCallback? = null,
    val imageView: ImageView? = null
) {
    
    /**
     * Builder class for creating ImageRequest instances.
     * Follows Builder pattern for flexible object creation.
     */
    class Builder(private val url: String) {
        private var placeholder: Int? = null
        private var errorImage: Int? = null
        private var targetWidth: Int? = null
        private var targetHeight: Int? = null
        private var centerCrop: Boolean = false
        private var centerInside: Boolean = false
        private var callback: ImageCallback? = null
        private var imageView: ImageView? = null
        
        /**
         * Set placeholder image resource
         */
        fun placeholder(resourceId: Int) = apply {
            this.placeholder = resourceId
        }
        
        /**
         * Set error image resource
         */
        fun error(resourceId: Int) = apply {
            this.errorImage = resourceId
        }
        
        /**
         * Set target dimensions for image resizing
         */
        fun resize(width: Int, height: Int) = apply {
            this.targetWidth = width
            this.targetHeight = height
        }
        
        /**
         * Enable center crop scaling
         */
        fun centerCrop() = apply {
            this.centerCrop = true
            this.centerInside = false
        }
        
        /**
         * Enable center inside scaling
         */
        fun centerInside() = apply {
            this.centerInside = true
            this.centerCrop = false
        }
        
        /**
         * Set callback for result handling
         */
        fun callback(callback: Any) = apply {
            this.callback = callback as ImageCallback
        }
        
        /**
         * Set target ImageView
         */
        fun into(imageView: ImageView) = apply {
            this.imageView = imageView
        }
        
        /**
         * Build the ImageRequest
         */
        fun build(): ImageRequest {
            return ImageRequest(
                url = url,
                placeholder = placeholder,
                errorImage = errorImage,
                targetWidth = targetWidth,
                targetHeight = targetHeight,
                centerCrop = centerCrop,
                centerInside = centerInside,
                callback = callback,
                imageView = imageView
            )
        }
    }
}
