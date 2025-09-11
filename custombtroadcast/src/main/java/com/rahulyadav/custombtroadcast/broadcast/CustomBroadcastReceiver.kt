package com.rahulyadav.custombtroadcast.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log

/**
 * Custom Broadcast Receiver interface that provides enhanced functionality
 * over the standard Android BroadcastReceiver
 */
interface CustomBroadcastReceiver {
    /**
     * Called when a broadcast is received
     * @param context The context in which the receiver is running
     * @param action The action of the broadcast
     * @param data The data bundle containing broadcast payload
     */
    fun onCustomBroadcastReceived(context: Context, action: String?, data: Bundle?)
    
    /**
     * Get the actions this receiver is interested in
     * @return List of action strings
     */
    fun getInterestedActions(): List<String>
    
    /**
     * Get the priority of this receiver (higher number = higher priority)
     * @return Priority value
     */
    fun getPriority(): Int = 0
    
    /**
     * Check if this receiver should handle the given action
     * @param action The broadcast action
     * @return true if this receiver should handle the action
     */
    fun shouldHandleAction(action: String?): Boolean {
        return getInterestedActions().contains(action)
    }
}

/**
 * Base implementation of CustomBroadcastReceiver that wraps Android's BroadcastReceiver
 */
abstract class BaseCustomBroadcastReceiver : BroadcastReceiver(), CustomBroadcastReceiver {
    
    companion object {
        private const val TAG = "BaseCustomBroadcastReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val data = intent.extras
        
        Log.d(TAG, "Received broadcast: $action")
        
        if (shouldHandleAction(action)) {
            onCustomBroadcastReceived(context, action, data)
        }
    }
    
    /**
     * Create an IntentFilter for this receiver
     * @return IntentFilter with all interested actions
     */
    fun createIntentFilter(): IntentFilter {
        val filter = IntentFilter()
        getInterestedActions().forEach { action ->
            filter.addAction(action)
        }
        filter.priority = getPriority()
        return filter
    }
}
