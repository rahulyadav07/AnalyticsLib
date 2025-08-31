package com.rahulyadav.analytics.privacy

data class PrivacySettings (
    val trackingEnabled: Boolean = true,
    val collectDeviceInfo: Boolean = true,
    val blockedEvents: Set<String> = emptySet(),
    val sensitiveParams: Set<String> = setOf("email", "phone", "password", "ssn")

)

