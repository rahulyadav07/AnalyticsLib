package com.rahulyadav.analytics.analytics

data class AnalyticsEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val params: Map<String, Any>?,
    val timestamp: Long = System.currentTimeMillis(),
)

data class DeviceInfo(
    val version: String?,
    val model: String? = null,
    val manufacturer: String? = null,
    val osVersion: String? = null
)