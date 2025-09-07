package com.rahulyadav.analytics.analytics

data class AnalyticsEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val params: Map<String, Any>?,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionId: String? = null, // No session management
    val userId: String?,
    val deviceInfo: DeviceInfo?
)

data class DeviceInfo(
    val version: String?,
    val model: String? = null,
    val manufacturer: String? = null,
    val osVersion: String? = null
)