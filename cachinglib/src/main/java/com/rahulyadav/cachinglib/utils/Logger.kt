package com.rahulyadav.cachinglib.utils

/**
 * Simple logging utility for the cache library.
 * Follows Single Responsibility Principle - only handles logging.
 */
object Logger {
    
    private const val TAG = "CacheLib"
    private var isEnabled = false
    
    /**
     * Enable or disable logging
     */
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }
    
    /**
     * Log debug message
     */
    fun d(message: String) {
        if (isEnabled) {
            android.util.Log.d(TAG, message)
        }
    }
    
    /**
     * Log info message
     */
    fun i(message: String) {
        if (isEnabled) {
            android.util.Log.i(TAG, message)
        }
    }
    
    /**
     * Log warning message
     */
    fun w(message: String) {
        if (isEnabled) {
            android.util.Log.w(TAG, message)
        }
    }
    
    /**
     * Log error message
     */
    fun e(message: String, throwable: Throwable? = null) {
        if (isEnabled) {
            android.util.Log.e(TAG, message, throwable)
        }
    }
}
