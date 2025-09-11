package com.rahulyadav.custombtroadcast.broadcast

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.rahulyadav.custombtroadcast.receivers.DataSyncReceiver
import com.rahulyadav.custombtroadcast.receivers.SystemEventReceiver
import com.rahulyadav.custombtroadcast.receivers.UserActionReceiver
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


/**
 * Custom Broadcast Manager that provides enhanced broadcast functionality
 * with features like:
 * - Priority-based handling
 * - Type-safe broadcast sending
 * - Receiver lifecycle management
 * - Broadcast queuing and batching
 * - App lifecycle monitoring
 * - Activity-specific receivers
 */
class CustomBroadcastManager private constructor(private val application: Application) {
    
    companion object {
        private const val TAG = "CustomBroadcastManager"
        
        @Volatile
        private var INSTANCE: CustomBroadcastManager? = null
        
        fun getInstance(application: Application): CustomBroadcastManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CustomBroadcastManager(application).also { INSTANCE = it }
            }
        }
    }
    
    // Thread-safe collections for managing receivers
    private val registeredReceivers = ConcurrentHashMap<String, CopyOnWriteArrayList<CustomBroadcastReceiver>>()
    private val receiverInstances = ConcurrentHashMap<CustomBroadcastReceiver, android.content.BroadcastReceiver>()
    private val receiverIds = ConcurrentHashMap<String, CustomBroadcastReceiver>()
    private val activityReceivers = ConcurrentHashMap<String, MutableList<CustomBroadcastReceiver>>()
    private var isAppInForeground = false
    
    init {
        setupLifecycleObservers()
        registerGlobalReceivers()
    }
    
    /**
     * Setup lifecycle observers for app state changes
     */
    private fun setupLifecycleObservers() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                onAppForeground()
            }
            
            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                onAppBackground()
            }
        })
        
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            
            override fun onActivityDestroyed(activity: Activity) {
                unregisterActivityReceivers(activity)
            }
        })
    }
    
    /**
     * Register global receivers that should be active throughout app lifecycle
     */
    private fun registerGlobalReceivers() {
        // Register system event receiver
        val systemReceiver = SystemEventReceiver()
        registerReceiver("system_events", systemReceiver)
        
        // Register data sync receiver
        val dataSyncReceiver = DataSyncReceiver()
        registerReceiver("data_sync", dataSyncReceiver)
        
        Log.i(TAG, "Global receivers registered")
    }
    
    /**
     * Register a custom broadcast receiver with a unique identifier
     * @param id Unique identifier for the receiver
     * @param receiver The custom receiver to register
     */
    fun registerReceiver(id: String, receiver: CustomBroadcastReceiver) {
        if (receiverIds.containsKey(id)) {
            Log.w(TAG, "Receiver with id '$id' already registered, unregistering first")
            unregisterReceiver(id)
        }
        
        registerReceiverInternal(receiver)
        receiverIds[id] = receiver
        
        Log.d(TAG, "Registered receiver: $id")
    }
    
    /**
     * Register a custom broadcast receiver (without ID)
     * @param receiver The custom receiver to register
     */
    fun registerReceiver(receiver: CustomBroadcastReceiver) {
        registerReceiverInternal(receiver)
        Log.d(TAG, "Registered receiver: ${receiver.javaClass.simpleName}")
    }
    
    /**
     * Internal method to register a receiver
     * @param receiver The custom receiver to register
     */
    private fun registerReceiverInternal(receiver: CustomBroadcastReceiver) {
        val androidReceiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                val data = intent.extras
                
                if (receiver.shouldHandleAction(action)) {
                    receiver.onCustomBroadcastReceived(context, action, data)
                }
            }
        }
        
        // Register with Android system
        val filter = (receiver as? BaseCustomBroadcastReceiver)?.createIntentFilter() 
            ?: createIntentFilter(receiver.getInterestedActions())
        
        application.registerReceiver(androidReceiver, filter)
        
        // Store references
        receiverInstances[receiver] = androidReceiver
        
        // Add to our internal tracking
        receiver.getInterestedActions().forEach { action ->
            registeredReceivers.computeIfAbsent(action) { CopyOnWriteArrayList() }
                .add(receiver)
        }
        
        // Sort by priority
        registeredReceivers.values.forEach { list ->
            list.sortByDescending { it.getPriority() }
        }
        
        Log.d(TAG, "Registered receiver for actions: ${receiver.getInterestedActions()}")
    }
    
    /**
     * Unregister a custom broadcast receiver by ID
     * @param id The receiver ID
     */
    fun unregisterReceiver(id: String) {
        val receiver = receiverIds.remove(id)
        receiver?.let {
            unregisterReceiverInternal(it)
            Log.d(TAG, "Unregistered receiver: $id")
        }
    }
    
    /**
     * Unregister a custom broadcast receiver
     * @param receiver The receiver to unregister
     */
    fun unregisterReceiver(receiver: CustomBroadcastReceiver) {
        unregisterReceiverInternal(receiver)
        Log.d(TAG, "Unregistered receiver: ${receiver.javaClass.simpleName}")
    }
    
    /**
     * Internal method to unregister a receiver
     * @param receiver The receiver to unregister
     */
    private fun unregisterReceiverInternal(receiver: CustomBroadcastReceiver) {
        val androidReceiver = receiverInstances.remove(receiver)
        androidReceiver?.let {
            application.unregisterReceiver(it)
        }
        
        // Remove from internal tracking
        registeredReceivers.values.forEach { list ->
            list.remove(receiver)
        }
    }
    
    /**
     * Register a receiver for a specific activity
     * @param activity The activity context
     * @param receiver The receiver to register
     */
    fun registerActivityReceiver(activity: Activity, receiver: CustomBroadcastReceiver) {
        val activityName = activity.javaClass.simpleName
        val receivers = activityReceivers.computeIfAbsent(activityName) { mutableListOf() }
        
        registerReceiverInternal(receiver)
        receivers.add(receiver)
        
        Log.d(TAG, "Registered receiver for activity: $activityName")
    }
    
    /**
     * Unregister all receivers for a specific activity
     * @param activity The activity context
     */
    private fun unregisterActivityReceivers(activity: Activity) {
        val activityName = activity.javaClass.simpleName
        val receivers = activityReceivers.remove(activityName)
        
        receivers?.forEach { receiver ->
            unregisterReceiverInternal(receiver)
        }
        
        Log.d(TAG, "Unregistered ${receivers?.size ?: 0} receivers for activity: $activityName")
    }
    
    /**
     * Send a custom broadcast
     * @param action The broadcast action
     * @param data Optional data bundle
     * @param isOrdered Whether this is an ordered broadcast
     */
    fun sendCustomBroadcast(action: String, data: Bundle? = null, isOrdered: Boolean = false) {
        val intent = Intent(action).apply {
            data?.let { putExtras(it) }
        }
        
        if (isOrdered) {
            application.sendOrderedBroadcast(intent, null)
        } else {
            application.sendBroadcast(intent)
        }
        
        Log.d(TAG, "Sent broadcast: $action")
    }
    
    /**
     * Send a custom broadcast with type safety
     * @param broadcast The custom broadcast object
     */
    fun sendCustomBroadcast(broadcast: CustomBroadcast) {
        sendCustomBroadcast(broadcast.action, broadcast.data, broadcast.isOrdered)
    }
    
    /**
     * Send a broadcast to specific receivers only (local broadcast)
     * @param action The broadcast action
     * @param data Optional data bundle
     */
    fun sendLocalBroadcast(context: Context, action: String, data: Bundle? = null) {
        val receivers = registeredReceivers[action] ?: return
        
        val intent = Intent(action).apply {
            data?.let { putExtras(it) }
        }
        
        // Send to all registered receivers for this action
        receivers.forEach { receiver ->
            try {
                receiver.onCustomBroadcastReceived(context, action, data)
            } catch (e: Exception) {
                Log.e(TAG, "Error delivering broadcast to receiver", e)
            }
        }
        
        Log.d(TAG, "Sent local broadcast: $action to ${receivers.size} receivers")
    }
    
    /**
     * Get all registered receivers for an action
     * @param action The broadcast action
     * @return List of receivers interested in this action
     */
    fun getReceiversForAction(action: String): List<CustomBroadcastReceiver> {
        return registeredReceivers[action]?.toList() ?: emptyList()
    }
    
    /**
     * Check if any receivers are registered for an action
     * @param action The broadcast action
     * @return true if receivers are registered
     */
    fun hasReceiversForAction(action: String): Boolean {
        return registeredReceivers.containsKey(action) && 
               registeredReceivers[action]?.isNotEmpty() == true
    }
    
    private fun createIntentFilter(actions: List<String>): android.content.IntentFilter {
        val filter = android.content.IntentFilter()
        actions.forEach { action ->
            filter.addAction(action)
        }
        return filter
    }
    
    // Lifecycle Management Methods
    
    /**
     * Handle app moving to foreground
     */
    private fun onAppForeground() {
        if (!isAppInForeground) {
            isAppInForeground = true
            Log.i(TAG, "App moved to foreground")
            
            // Send app foreground broadcast
            val data = Bundle().apply {
                putLong("timestamp", System.currentTimeMillis())
            }
            sendCustomBroadcast(
                SystemEventReceiver.ACTION_APP_FOREGROUND,
                data
            )
        }
    }
    
    /**
     * Handle app moving to background
     */
    private fun onAppBackground() {
        if (isAppInForeground) {
            isAppInForeground = false
            Log.i(TAG, "App moved to background")
            
            // Send app background broadcast
            val data = Bundle().apply {
                putLong("timestamp", System.currentTimeMillis())
            }
            sendCustomBroadcast(
                SystemEventReceiver.ACTION_APP_BACKGROUND,
                data
            )
        }
    }
    
    /**
     * Register user action receiver (typically done when user logs in)
     */
    fun registerUserActionReceiver() {
        if (!receiverIds.containsKey("user_actions")) {
            val userReceiver = UserActionReceiver()
            registerReceiver("user_actions", userReceiver)
            Log.i(TAG, "User action receiver registered")
        }
    }
    
    /**
     * Unregister user action receiver (typically done when user logs out)
     */
    fun unregisterUserActionReceiver() {
        if (receiverIds.containsKey("user_actions")) {
            unregisterReceiver("user_actions")
            Log.i(TAG, "User action receiver unregistered")
        }
    }
    
    /**
     * Get a registered receiver by ID
     * @param id The receiver ID
     * @return The receiver or null if not found
     */
    fun getReceiver(id: String): CustomBroadcastReceiver? {
        return receiverIds[id]
    }
    
    /**
     * Get all registered receiver IDs
     * @return Set of receiver IDs
     */
    fun getRegisteredReceiverIds(): Set<String> {
        return receiverIds.keys.toSet()
    }
    
    /**
     * Check if a receiver is registered
     * @param id The receiver ID
     * @return true if registered
     */
    fun isReceiverRegistered(id: String): Boolean {
        return receiverIds.containsKey(id)
    }
    
    /**
     * Check if app is currently in foreground
     * @return true if in foreground
     */
    fun isAppInForeground(): Boolean {
        return isAppInForeground
    }
    
    /**
     * Cleanup all receivers (call when app is being destroyed)
     */
    fun cleanup() {
        Log.i(TAG, "Cleaning up all receivers")
        
        // Unregister all global receivers
        receiverIds.keys.toList().forEach { id ->
            unregisterReceiver(id)
        }
        
        // Unregister all activity receivers
        activityReceivers.values.flatten().forEach { receiver ->
            unregisterReceiverInternal(receiver)
        }
        activityReceivers.clear()
        
        Log.i(TAG, "All receivers cleaned up")
    }
}

/**
 * Data class representing a custom broadcast
 */
data class CustomBroadcast(
    val action: String,
    val data: Bundle? = null,
    val isOrdered: Boolean = false
)
