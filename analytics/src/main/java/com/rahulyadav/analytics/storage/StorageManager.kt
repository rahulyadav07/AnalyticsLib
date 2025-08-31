package com.rahulyadav.analytics.storage

import com.rahulyadav.analytics.analytics.AnalyticsEvent

interface StorageManager {
    suspend fun persistEvents(events: List<AnalyticsEvent>)
    suspend fun loadPersistedEvents(): List<AnalyticsEvent>
    suspend fun clearPersistedEvents()
    fun saveUserProperty(property: UserProperty)
    fun saveUserId(userId: String)


}