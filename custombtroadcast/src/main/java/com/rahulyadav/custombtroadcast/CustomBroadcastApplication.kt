package com.rahulyadav.custombtroadcast

import android.app.Application
import android.util.Log
import com.rahulyadav.custombtroadcast.broadcast.CustomBroadcastManager

/**
 * Application class that initializes the custom broadcast system
 * This ensures the broadcast system is available throughout the app lifecycle
 */
class CustomBroadcastApplication : Application() {
    
    companion object {
        private const val TAG = "CustomBroadcastApplication"
    }
    
    private lateinit var broadcastManager: CustomBroadcastManager
    
    override fun onCreate() {
        super.onCreate()
        
        Log.i(TAG, "Initializing Custom Broadcast Application")
        
        // Initialize the broadcast manager
        broadcastManager = CustomBroadcastManager.getInstance(this)
        
        Log.i(TAG, "Custom Broadcast System initialized successfully")
    }
    
    /**
     * Get the broadcast manager instance
     * @return CustomBroadcastManager instance
     */
    fun getBroadcastManager(): CustomBroadcastManager {
        return broadcastManager
    }
    
    override fun onTerminate() {
        super.onTerminate()
        
        Log.i(TAG, "Application terminating - cleaning up broadcast system")
        
        // Cleanup broadcast receivers
        broadcastManager.cleanup()
        
        Log.i(TAG, "Broadcast system cleanup completed")
    }
}
