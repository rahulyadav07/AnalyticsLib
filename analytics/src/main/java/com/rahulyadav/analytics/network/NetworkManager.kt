package com.rahulyadav.analytics.network

import com.rahulyadav.analytics.analytics.AnalyticsEvent

interface NetworkManager {
    suspend fun sendEvents(event:List<AnalyticsEvent>)
}