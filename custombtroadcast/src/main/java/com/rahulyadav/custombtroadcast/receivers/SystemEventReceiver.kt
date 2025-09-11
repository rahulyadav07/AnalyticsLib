package com.rahulyadav.custombtroadcast.receivers

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.rahulyadav.custombtroadcast.broadcast.BaseCustomBroadcastReceiver

/**
 * Example receiver for handling system-level events
 * Demonstrates medium-priority system event handling
 */
class SystemEventReceiver : BaseCustomBroadcastReceiver() {
    
    companion object {
        private const val TAG = "SystemEventReceiver"
        
        // Custom action constants
        const val ACTION_APP_BACKGROUND = "com.rahulyadav.custombtroadcast.APP_BACKGROUND"
        const val ACTION_APP_FOREGROUND = "com.rahulyadav.custombtroadcast.APP_FOREGROUND"
        const val ACTION_NETWORK_STATE_CHANGE = "com.rahulyadav.custombtroadcast.NETWORK_STATE_CHANGE"
        const val ACTION_BATTERY_LOW = "com.rahulyadav.custombtroadcast.BATTERY_LOW"
        const val ACTION_STORAGE_LOW = "com.rahulyadav.custombtroadcast.STORAGE_LOW"
        
        // Data keys
        const val KEY_NETWORK_TYPE = "network_type"
        const val KEY_IS_CONNECTED = "is_connected"
        const val KEY_BATTERY_LEVEL = "battery_level"
        const val KEY_STORAGE_AVAILABLE = "storage_available"
        const val KEY_TIMESTAMP = "timestamp"
    }
    
    override fun getInterestedActions(): List<String> {
        return listOf(
            ACTION_APP_BACKGROUND,
            ACTION_APP_FOREGROUND,
            ACTION_NETWORK_STATE_CHANGE,
            ACTION_BATTERY_LOW,
            ACTION_STORAGE_LOW
        )
    }
    
    override fun getPriority(): Int = 500 // Medium priority for system events

    override fun onCustomBroadcastReceived(context: Context, action: String?, data: Bundle?) {
        when (action) {
            ACTION_APP_BACKGROUND -> handleAppBackground(context, data)
            ACTION_APP_FOREGROUND -> handleAppForeground(context, data)
            ACTION_NETWORK_STATE_CHANGE -> handleNetworkChange(context, data)
            ACTION_BATTERY_LOW -> handleBatteryLow(context, data)
            ACTION_STORAGE_LOW -> handleStorageLow(context, data)
        }
    }
    
    private fun handleAppBackground(context: Context, data: Bundle?) {
        val timestamp = data?.getLong(KEY_TIMESTAMP, System.currentTimeMillis()) ?: System.currentTimeMillis()
        
        Log.i(TAG, "App moved to background at $timestamp")
        
        // Example: Pause non-essential services, save state, etc.
        pauseBackgroundTasks(context)
        saveAppState(context)
        trackSystemEvent(context, "app_background", timestamp)
    }
    
    private fun handleAppForeground(context: Context, data: Bundle?) {
        val timestamp = data?.getLong(KEY_TIMESTAMP, System.currentTimeMillis()) ?: System.currentTimeMillis()
        
        Log.i(TAG, "App moved to foreground at $timestamp")
        
        // Example: Resume services, refresh data, etc.
        resumeBackgroundTasks(context)
        refreshAppData(context)
        trackSystemEvent(context, "app_foreground", timestamp)
    }
    
    private fun handleNetworkChange(context: Context, data: Bundle?) {
        val networkType = data?.getString(KEY_NETWORK_TYPE) ?: "unknown"
        val isConnected = data?.getBoolean(KEY_IS_CONNECTED, false) ?: false
        val timestamp = data?.getLong(KEY_TIMESTAMP, System.currentTimeMillis()) ?: System.currentTimeMillis()
        
        Log.i(TAG, "Network state changed: $networkType, connected: $isConnected at $timestamp")
        
        if (isConnected) {
            // Example: Sync pending data, refresh content, etc.
            syncPendingData(context)
            refreshContent(context)
        } else {
            // Example: Queue operations, show offline indicator, etc.
            queueOfflineOperations(context)
            showOfflineIndicator(context)
        }
        
        trackSystemEvent(context, "network_change", timestamp)
    }
    
    private fun handleBatteryLow(context: Context, data: Bundle?) {
        val batteryLevel = data?.getInt(KEY_BATTERY_LEVEL, 0) ?: 0
        val timestamp = data?.getLong(KEY_TIMESTAMP, System.currentTimeMillis()) ?: System.currentTimeMillis()
        
        Log.w(TAG, "Battery low: $batteryLevel% at $timestamp")
        
        // Example: Reduce background activity, optimize performance, etc.
        reduceBackgroundActivity(context)
        optimizePerformance(context)
        showBatteryWarning(context, batteryLevel)
        
        trackSystemEvent(context, "battery_low", timestamp)
    }
    
    private fun handleStorageLow(context: Context, data: Bundle?) {
        val storageAvailable = data?.getLong(KEY_STORAGE_AVAILABLE, 0L) ?: 0L
        val timestamp = data?.getLong(KEY_TIMESTAMP, System.currentTimeMillis()) ?: System.currentTimeMillis()
        
        Log.w(TAG, "Storage low: ${storageAvailable / (1024 * 1024)}MB available at $timestamp")
        
        // Example: Clear cache, compress data, etc.
        clearCache(context)
        compressStoredData(context)
        showStorageWarning(context, storageAvailable)
        
        trackSystemEvent(context, "storage_low", timestamp)
    }
    
    // Helper methods for system event handling
    private fun pauseBackgroundTasks(context: Context) {
        Log.d(TAG, "Pausing background tasks")
        // Example: Stop location updates, pause sync, etc.
    }
    
    private fun resumeBackgroundTasks(context: Context) {
        Log.d(TAG, "Resuming background tasks")
        // Example: Resume location updates, restart sync, etc.
    }
    
    private fun saveAppState(context: Context) {
        Log.d(TAG, "Saving app state")
        // Example: Save user preferences, cache important data, etc.
    }
    
    private fun refreshAppData(context: Context) {
        Log.d(TAG, "Refreshing app data")
        // Example: Fetch latest data from server, update UI, etc.
    }
    
    private fun syncPendingData(context: Context) {
        Log.d(TAG, "Syncing pending data")
        // Example: Upload queued data, download updates, etc.
    }
    
    private fun refreshContent(context: Context) {
        Log.d(TAG, "Refreshing content")
        // Example: Update news feed, refresh user data, etc.
    }
    
    private fun queueOfflineOperations(context: Context) {
        Log.d(TAG, "Queuing offline operations")
        // Example: Store operations for later execution, etc.
    }
    
    private fun showOfflineIndicator(context: Context) {
        Log.d(TAG, "Showing offline indicator")
        // Example: Update UI to show offline status, etc.
    }
    
    private fun reduceBackgroundActivity(context: Context) {
        Log.d(TAG, "Reducing background activity")
        // Example: Stop non-essential services, reduce sync frequency, etc.
    }
    
    private fun optimizePerformance(context: Context) {
        Log.d(TAG, "Optimizing performance")
        // Example: Reduce animation quality, limit background processing, etc.
    }
    
    private fun showBatteryWarning(context: Context, batteryLevel: Int) {
        Log.d(TAG, "Showing battery warning for level: $batteryLevel%")
        // Example: Show notification or dialog about battery level, etc.
    }
    
    private fun clearCache(context: Context) {
        Log.d(TAG, "Clearing cache")
        // Example: Clear image cache, temporary files, etc.
    }
    
    private fun compressStoredData(context: Context) {
        Log.d(TAG, "Compressing stored data")
        // Example: Compress database, reduce image quality, etc.
    }
    
    private fun showStorageWarning(context: Context, storageAvailable: Long) {
        Log.d(TAG, "Showing storage warning for available: ${storageAvailable / (1024 * 1024)}MB")
        // Example: Show notification about low storage, etc.
    }
    
    private fun trackSystemEvent(context: Context, event: String, timestamp: Long) {
        Log.d(TAG, "Tracking system event: $event at $timestamp")
        // Example: Send to analytics service, log to file, etc.
    }
}
