package com.rahulyadav.custombtroadcast.receivers

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.rahulyadav.custombtroadcast.broadcast.BaseCustomBroadcastReceiver

/**
 * Example receiver for handling user action broadcasts
 * Demonstrates high-priority user interaction handling
 */
class UserActionReceiver : BaseCustomBroadcastReceiver() {
    
    companion object {
        private const val TAG = "UserActionReceiver"
        
        // Custom action constants
        const val ACTION_USER_LOGIN = "com.rahulyadav.custombtroadcast.USER_LOGIN"
        const val ACTION_USER_LOGOUT = "com.rahulyadav.custombtroadcast.USER_LOGOUT"
        const val ACTION_USER_PROFILE_UPDATE = "com.rahulyadav.custombtroadcast.USER_PROFILE_UPDATE"
        
        // Data keys
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_TIMESTAMP = "timestamp"
    }



    override fun getInterestedActions(): List<String> {
        return listOf(
            ACTION_USER_LOGIN,
            ACTION_USER_LOGOUT,
            ACTION_USER_PROFILE_UPDATE
        )
    }
    
    override fun getPriority(): Int = 1000 // High priority for user actions

    override fun onCustomBroadcastReceived(context: Context, action: String?, data: Bundle?) {
        when (action) {
            ACTION_USER_LOGIN -> handleUserLogin(context, data)
            ACTION_USER_LOGOUT -> handleUserLogout(context, data)
            ACTION_USER_PROFILE_UPDATE -> handleProfileUpdate(context, data)
        }
    }
    
    private fun handleUserLogin(context: Context, data: Bundle?) {
        val userId = data?.getString(KEY_USER_ID) ?: "unknown"
        val userName = data?.getString(KEY_USER_NAME) ?: "unknown"
        val timestamp = data?.getLong(KEY_TIMESTAMP, System.currentTimeMillis()) ?: System.currentTimeMillis()
        
        Log.i(TAG, "User login: $userName (ID: $userId) at $timestamp")
        
        // Example: Update user session, analytics, etc.
        // This could trigger other system components
        updateUserSession(context, userId, true)
        trackUserEvent(context, "login", userId)
    }
    
    private fun handleUserLogout(context: Context, data: Bundle?) {
        val userId = data?.getString(KEY_USER_ID) ?: "unknown"
        val timestamp = data?.getLong(KEY_TIMESTAMP, System.currentTimeMillis()) ?: System.currentTimeMillis()
        
        Log.i(TAG, "User logout: ID $userId at $timestamp")
        
        // Example: Clear user session, save data, etc.
        updateUserSession(context, userId, false)
        trackUserEvent(context, "logout", userId)
    }
    
    private fun handleProfileUpdate(context: Context, data: Bundle?) {
        val userId = data?.getString(KEY_USER_ID) ?: "unknown"
        val userName = data?.getString(KEY_USER_NAME)
        val userEmail = data?.getString(KEY_USER_EMAIL)
        
        Log.i(TAG, "Profile update for user: $userId")
        
        // Example: Update local cache, sync with server, etc.
        if (userName != null) {
            updateUserCache(context, userId, "name", userName)
        }
        if (userEmail != null) {
            updateUserCache(context, userId, "email", userEmail)
        }
        
        trackUserEvent(context, "profile_update", userId)
    }
    
    private fun updateUserSession(context: Context, userId: String, isLoggedIn: Boolean) {
        // Example implementation - could use SharedPreferences, Room DB, etc.
        val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("is_logged_in", isLoggedIn)
            .putString("user_id", if (isLoggedIn) userId else null)
            .putLong("last_activity", System.currentTimeMillis())
            .apply()
    }
    
    private fun trackUserEvent(context: Context, event: String, userId: String) {
        // Example: Send to analytics service
        Log.d(TAG, "Tracking event: $event for user: $userId")
        // Could integrate with analytics libraries like Firebase, Mixpanel, etc.
    }
    
    private fun updateUserCache(context: Context, userId: String, field: String, value: String) {
        // Example: Update local user cache
        val prefs = context.getSharedPreferences("user_cache_$userId", Context.MODE_PRIVATE)
        prefs.edit().putString(field, value).apply()
        Log.d(TAG, "Updated user cache: $field = $value for user: $userId")
    }
}
