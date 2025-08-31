package com.rahulyadav.analytics.analytics

data class AnalyticsConfig(
    val batchSize:Int? = 50,
    val batchTimeInterval:Long = 5*60*60,
    val maxQueueSize: Int = 1000,
    val maxRetryAttempts: Int = 3,
    val providers: List<AnalyticsProvider> = emptyList()

)
