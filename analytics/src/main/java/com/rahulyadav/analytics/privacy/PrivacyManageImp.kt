package com.rahulyadav.analytics.privacy

import com.rahulyadav.analytics.analytics.AnalyticsEvent

class PrivacyManageImp(private val settings: PrivacySettings):PrivacyManager {
    override fun isTrackingEnabled(): Boolean = settings.trackingEnabled

    override fun canTrackEvent(eventName: String): Boolean {
        return settings.trackingEnabled && !settings.blockedEvents.contains(eventName)
    }

    override fun shouldCollectDeviceInfo(): Boolean = settings.collectDeviceInfo

    override fun applyPrivacyFilters(event: AnalyticsEvent): AnalyticsEvent {
        if (!settings.trackingEnabled) return event

        val filteredParams = event.params?.filterKeys { key ->
            !settings.sensitiveParams.contains(key.lowercase())
        }

        return event.copy(
            params = filteredParams,
            deviceInfo = if (settings.collectDeviceInfo) event.deviceInfo else null
        )
    }
}