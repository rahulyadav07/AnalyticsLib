package com.rahulyadav.custombtroadcast.receivers

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.rahulyadav.custombtroadcast.broadcast.BaseCustomBroadcastReceiver

/**
 * UI Update Receiver that handles UI-related broadcast events
 * This receiver is designed to update the UI when certain events occur
 */
class UiUpdateReceiver : BaseCustomBroadcastReceiver() {
    
    companion object {
        private const val TAG = "UiUpdateReceiver"
        
        // Custom action constants for UI updates
        const val ACTION_UI_UPDATE_REQUEST = "com.rahulyadav.custombtroadcast.UI_UPDATE_REQUEST"
        const val ACTION_UI_LOG_MESSAGE = "com.rahulyadav.custombtroadcast.UI_LOG_MESSAGE"
        
        // Data keys
        const val KEY_MESSAGE = "message"
        const val KEY_LOG_TYPE = "log_type"
        const val KEY_TIMESTAMP = "timestamp"
    }
    
    // Callback interface for UI updates
    interface UiUpdateCallback {
        fun onLogMessage(message: String, logType: String = "INFO")
        fun onUiUpdateRequest(data: Bundle?)
    }
    
    private var uiCallback: UiUpdateCallback? = null
    
    override fun getInterestedActions(): List<String> {
        return listOf(
            ACTION_UI_UPDATE_REQUEST,
            ACTION_UI_LOG_MESSAGE,
            UserActionReceiver.ACTION_USER_LOGIN,
            UserActionReceiver.ACTION_USER_LOGOUT,
            SystemEventReceiver.ACTION_APP_FOREGROUND,
            SystemEventReceiver.ACTION_APP_BACKGROUND
        )
    }
    
    override fun getPriority(): Int = 2000 // Very high priority for UI updates
    
    override fun onCustomBroadcastReceived(context: Context, action: String?, data: Bundle?) {
        when (action) {
            ACTION_UI_LOG_MESSAGE -> handleLogMessage(data)
            ACTION_UI_UPDATE_REQUEST -> handleUiUpdateRequest(data)
            UserActionReceiver.ACTION_USER_LOGIN -> handleUserLogin(data)
            UserActionReceiver.ACTION_USER_LOGOUT -> handleUserLogout(data)
            SystemEventReceiver.ACTION_APP_FOREGROUND -> handleAppForeground(data)
            SystemEventReceiver.ACTION_APP_BACKGROUND -> handleAppBackground(data)
        }
    }
    
    /**
     * Set the UI callback for handling UI updates
     * @param callback The callback interface
     */
    fun setUiCallback(callback: UiUpdateCallback?) {
        this.uiCallback = callback
    }
    
    private fun handleLogMessage(data: Bundle?) {
        val message = data?.getString(KEY_MESSAGE) ?: "Unknown message"
        val logType = data?.getString(KEY_LOG_TYPE) ?: "INFO"
        
        Log.d(TAG, "UI Log: [$logType] $message")
        uiCallback?.onLogMessage(message, logType)
    }
    
    private fun handleUiUpdateRequest(data: Bundle?) {
        Log.d(TAG, "UI Update Request received")
        uiCallback?.onUiUpdateRequest(data)
    }
    
    private fun handleUserLogin(data: Bundle?) {
        val userId = data?.getString(UserActionReceiver.KEY_USER_ID) ?: "unknown"
        val message = "UI: User logged in - $userId"
        
        Log.d(TAG, message)
        uiCallback?.onLogMessage(message, "USER_ACTION")
    }
    
    private fun handleUserLogout(data: Bundle?) {
        val userId = data?.getString(UserActionReceiver.KEY_USER_ID) ?: "unknown"
        val message = "UI: User logged out - $userId"
        
        Log.d(TAG, message)
        uiCallback?.onLogMessage(message, "USER_ACTION")
    }
    
    private fun handleAppForeground(data: Bundle?) {
        val message = "UI: App moved to foreground"
        
        Log.d(TAG, message)
        uiCallback?.onLogMessage(message, "SYSTEM_EVENT")
    }
    
    private fun handleAppBackground(data: Bundle?) {
        val message = "UI: App moved to background"
        
        Log.d(TAG, message)
        uiCallback?.onLogMessage(message, "SYSTEM_EVENT")
    }
}
