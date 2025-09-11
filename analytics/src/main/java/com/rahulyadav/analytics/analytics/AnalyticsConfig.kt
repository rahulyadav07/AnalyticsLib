package com.rahulyadav.analytics.analytics

data class AnalyticsConfig(
    val batchSize:Int? = 50,
    val maxQueueSize: Int = 10,
    val maxRetryAttempts: Int = 2,
    val maxDatabaseSize: Int = 10000  // Maximum events in database (FIFO eviction)
)
