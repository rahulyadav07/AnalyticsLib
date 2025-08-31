package com.rahulyadav.analytics.analytics

interface AnalyticsProvider {
    suspend fun sendEvents(events: List<AnalyticsEvent>): Boolean
    fun getName(): String
}
