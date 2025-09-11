package com.rahulyadav.custombtroadcast

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rahulyadav.custombtroadcast.broadcast.CustomBroadcastManager
import com.rahulyadav.custombtroadcast.receivers.DataSyncReceiver
import com.rahulyadav.custombtroadcast.receivers.SystemEventReceiver
import com.rahulyadav.custombtroadcast.receivers.UiUpdateReceiver
import com.rahulyadav.custombtroadcast.receivers.UserActionReceiver

/**
 * Main Activity demonstrating the custom broadcast receiver system
 * Shows how to send and receive custom broadcasts
 */
class MainActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private lateinit var broadcastManager: CustomBroadcastManager
    private lateinit var logTextView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeBroadcastSystem()
        setupUI()
    }
    
    /**
     * Initialize the broadcast system
     */
    private fun initializeBroadcastSystem() {
        broadcastManager = CustomBroadcastManager.getInstance(application)
        
        // Set up UI callback for the global UI receiver
        val uiReceiver = broadcastManager.getUiUpdateReceiver()
        uiReceiver?.setUiCallback(object : UiUpdateReceiver.UiUpdateCallback {
            override fun onLogMessage(message: String, logType: String) {
                runOnUiThread {
                    updateLog("[$logType] $message")
                }
            }
            
            override fun onUiUpdateRequest(data: Bundle?) {
                runOnUiThread {
                    updateLog("UI Update Request received")
                }
            }
        })
        
        Log.i(TAG, "Broadcast system initialized")
    }
    
    /**
     * Setup UI components and event listeners
     */
    private fun setupUI() {
        logTextView = findViewById(R.id.logTextView)
        
        // User Action Buttons
        findViewById<Button>(R.id.btnUserLogin).setOnClickListener {
            sendUserLoginBroadcast()
        }
        
        findViewById<Button>(R.id.btnUserLogout).setOnClickListener {
            sendUserLogoutBroadcast()
        }
        
        findViewById<Button>(R.id.btnProfileUpdate).setOnClickListener {
            sendProfileUpdateBroadcast()
        }
        
        // System Event Buttons
        findViewById<Button>(R.id.btnNetworkChange).setOnClickListener {
            sendNetworkChangeBroadcast()
        }
        
        findViewById<Button>(R.id.btnBatteryLow).setOnClickListener {
            sendBatteryLowBroadcast()
        }
        
        findViewById<Button>(R.id.btnStorageLow).setOnClickListener {
            sendStorageLowBroadcast()
        }
        
        // Data Sync Buttons
        findViewById<Button>(R.id.btnDataSyncRequest).setOnClickListener {
            sendDataSyncRequestBroadcast()
        }
        
        findViewById<Button>(R.id.btnDataSyncComplete).setOnClickListener {
            sendDataSyncCompleteBroadcast()
        }
        
        findViewById<Button>(R.id.btnDataSyncFailed).setOnClickListener {
            sendDataSyncFailedBroadcast()
        }
        
        findViewById<Button>(R.id.btnCacheUpdate).setOnClickListener {
            sendCacheUpdateBroadcast()
        }
        
        // System Control Buttons
        findViewById<Button>(R.id.btnRegisterUserReceiver).setOnClickListener {
            registerUserActionReceiver()
        }
        
        findViewById<Button>(R.id.btnUnregisterUserReceiver).setOnClickListener {
            unregisterUserActionReceiver()
        }
        
        findViewById<Button>(R.id.btnClearLog).setOnClickListener {
            clearLog()
        }
        
        updateLog("App started - Custom Broadcast System Ready")
    }
    
    
    // User Action Broadcast Methods
    private fun sendUserLoginBroadcast() {
        val data = Bundle().apply {
            putString(UserActionReceiver.KEY_USER_ID, "user_123")
            putString(UserActionReceiver.KEY_USER_NAME, "John Doe")
            putString(UserActionReceiver.KEY_USER_EMAIL, "john@example.com")
            putLong(UserActionReceiver.KEY_TIMESTAMP, System.currentTimeMillis())
        }
        
        broadcastManager.sendCustomBroadcast(UserActionReceiver.ACTION_USER_LOGIN, data)
        updateLog("Sent: User Login Broadcast")
    }
    
    private fun sendUserLogoutBroadcast() {
        val data = Bundle().apply {
            putString(UserActionReceiver.KEY_USER_ID, "user_123")
            putLong(UserActionReceiver.KEY_TIMESTAMP, System.currentTimeMillis())
        }
        
        broadcastManager.sendCustomBroadcast(UserActionReceiver.ACTION_USER_LOGOUT, data)
        updateLog("Sent: User Logout Broadcast")
    }
    
    private fun sendProfileUpdateBroadcast() {
        val data = Bundle().apply {
            putString(UserActionReceiver.KEY_USER_ID, "user_123")
            putString(UserActionReceiver.KEY_USER_NAME, "John Smith")
            putString(UserActionReceiver.KEY_USER_EMAIL, "johnsmith@example.com")
            putLong(UserActionReceiver.KEY_TIMESTAMP, System.currentTimeMillis())
        }
        
        broadcastManager.sendCustomBroadcast(UserActionReceiver.ACTION_USER_PROFILE_UPDATE, data)
        updateLog("Sent: Profile Update Broadcast")
    }
    
    // System Event Broadcast Methods
    private fun sendNetworkChangeBroadcast() {
        val data = Bundle().apply {
            putString(SystemEventReceiver.KEY_NETWORK_TYPE, "WiFi")
            putBoolean(SystemEventReceiver.KEY_IS_CONNECTED, true)
            putLong(SystemEventReceiver.KEY_TIMESTAMP, System.currentTimeMillis())
        }
        
        broadcastManager.sendCustomBroadcast(SystemEventReceiver.ACTION_NETWORK_STATE_CHANGE, data)
        updateLog("Sent: Network Change Broadcast")
    }
    
    private fun sendBatteryLowBroadcast() {
        val data = Bundle().apply {
            putInt(SystemEventReceiver.KEY_BATTERY_LEVEL, 15)
            putLong(SystemEventReceiver.KEY_TIMESTAMP, System.currentTimeMillis())
        }
        
        broadcastManager.sendCustomBroadcast(SystemEventReceiver.ACTION_BATTERY_LOW, data)
        updateLog("Sent: Battery Low Broadcast")
    }
    
    private fun sendStorageLowBroadcast() {
        val data = Bundle().apply {
            putLong(SystemEventReceiver.KEY_STORAGE_AVAILABLE, 50 * 1024 * 1024) // 50MB
            putLong(SystemEventReceiver.KEY_TIMESTAMP, System.currentTimeMillis())
        }
        
        broadcastManager.sendCustomBroadcast(SystemEventReceiver.ACTION_STORAGE_LOW, data)
        updateLog("Sent: Storage Low Broadcast")
    }
    
    // Data Sync Broadcast Methods
    private fun sendDataSyncRequestBroadcast() {
        val data = Bundle().apply {
            putString(DataSyncReceiver.KEY_SYNC_TYPE, "user_data")
            putLong(DataSyncReceiver.KEY_TIMESTAMP, System.currentTimeMillis())
        }
        
        broadcastManager.sendCustomBroadcast(DataSyncReceiver.ACTION_DATA_SYNC_REQUEST, data)
        updateLog("Sent: Data Sync Request Broadcast")
    }
    
    private fun sendDataSyncCompleteBroadcast() {
        val data = Bundle().apply {
            putString(DataSyncReceiver.KEY_SYNC_TYPE, "user_data")
            putInt(DataSyncReceiver.KEY_SUCCESS_COUNT, 25)
            putLong(DataSyncReceiver.KEY_DATA_SIZE, 1024 * 1024) // 1MB
            putLong(DataSyncReceiver.KEY_TIMESTAMP, System.currentTimeMillis())
        }
        
        broadcastManager.sendCustomBroadcast(DataSyncReceiver.ACTION_DATA_SYNC_COMPLETE, data)
        updateLog("Sent: Data Sync Complete Broadcast")
    }
    
    private fun sendDataSyncFailedBroadcast() {
        val data = Bundle().apply {
            putString(DataSyncReceiver.KEY_SYNC_TYPE, "user_data")
            putString(DataSyncReceiver.KEY_ERROR_MESSAGE, "Network timeout")
            putInt(DataSyncReceiver.KEY_FAILED_COUNT, 3)
            putLong(DataSyncReceiver.KEY_TIMESTAMP, System.currentTimeMillis())
        }
        
        broadcastManager.sendCustomBroadcast(DataSyncReceiver.ACTION_DATA_SYNC_FAILED, data)
        updateLog("Sent: Data Sync Failed Broadcast")
    }
    
    private fun sendCacheUpdateBroadcast() {
        val data = Bundle().apply {
            putString(DataSyncReceiver.KEY_CACHE_KEY, "user_profile")
            putString(DataSyncReceiver.KEY_CACHE_DATA, "{\"name\":\"John\",\"email\":\"john@example.com\"}")
            putLong(DataSyncReceiver.KEY_TIMESTAMP, System.currentTimeMillis())
        }
        
        broadcastManager.sendCustomBroadcast(DataSyncReceiver.ACTION_CACHE_UPDATE, data)
        updateLog("Sent: Cache Update Broadcast")
    }
    
    // System Control Methods
    private fun registerUserActionReceiver() {
        broadcastManager.registerUserActionReceiver()
        updateLog("Registered: User Action Receiver")
    }
    
    private fun unregisterUserActionReceiver() {
        broadcastManager.unregisterUserActionReceiver()
        updateLog("Unregistered: User Action Receiver")
    }
    
    private fun clearLog() {
        logTextView.text = ""
        updateLog("Log cleared")
    }
    
    /**
     * Update the log display
     */
    private fun updateLog(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        val logEntry = "[$timestamp] $message\n"
        
        runOnUiThread {
            logTextView.append(logEntry)
            // Auto-scroll to bottom
            logTextView.post {
                val scrollAmount = logTextView.layout?.getLineTop(logTextView.lineCount) ?: 0
                logTextView.scrollTo(0, scrollAmount)
            }
        }
        
        Log.d(TAG, message)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "MainActivity destroyed")
    }
}
