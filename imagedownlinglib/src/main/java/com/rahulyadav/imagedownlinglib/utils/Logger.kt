package com.rahulyadav.imagedownlinglib.utils

/**
 * Simple logging utility for the ImageLoader library.
 * Follows Single Responsibility Principle - only handles logging.
 */
object Logger {
    
    private var isEnabled = false
    
    /**
     * Enable or disable logging
     * @param enabled true to enable logging, false to disable
     */
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }
    
    /**
     * Log debug message
     * @param tag The log tag
     * @param message The message to log
     */
    fun d(tag: String, message: String) {
        if (isEnabled) {
            println("ImageLoader[$tag]: $message")
        }
    }
    
    /**
     * Log info message
     * @param tag The log tag
     * @param message The message to log
     */
    fun i(tag: String, message: String) {
        if (isEnabled) {
            println("ImageLoader[$tag]: $message")
        }
    }
    
    /**
     * Log warning message
     * @param tag The log tag
     * @param message The message to log
     */
    fun w(tag: String, message: String) {
        if (isEnabled) {
            println("ImageLoader[$tag]: WARNING - $message")
        }
    }
    
    /**
     * Log error message
     * @param tag The log tag
     * @param message The message to log
     * @param throwable Optional throwable to log
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (isEnabled) {
            println("ImageLoader[$tag]: ERROR - $message")
            throwable?.let {
                println("ImageLoader[$tag]: Exception: ${it.message}")
                it.printStackTrace()
            }
        }
    }
}
