package com.rahulyadav.analytics.analytics

data  class AnalyticsEvent(
    val name: String,
    val params: Map<String, Any>?,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionId: String?,
    val userId: String?,
    val deviceInfo: DeviceInfo?


)

data class DeviceInfo (
    val version:String?
)