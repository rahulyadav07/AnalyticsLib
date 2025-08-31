package com.rahulyadav.analytics.privacy

import com.rahulyadav.analytics.analytics.AnalyticsEvent

interface PrivacyManager {
    fun isTrackingEnabled(): Boolean
    fun canTrackEvent(eventName: String): Boolean
    fun shouldCollectDeviceInfo(): Boolean
    fun applyPrivacyFilters(event: AnalyticsEvent): AnalyticsEvent?
}