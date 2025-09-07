package com.rahulyadav.analytics.network

import com.rahulyadav.analytics.ExponentialBackoffRetry
import com.rahulyadav.analytics.analytics.AnalyticsConfig
import com.rahulyadav.analytics.analytics.AnalyticsEvent

class NetworkManagerImp(config:AnalyticsConfig):NetworkManager {

    private val retryPolicy = ExponentialBackoffRetry(config.maxRetryAttempts)
    private val provider = RetrofitAnalyticProvider() // Single hardcoded provider

    override suspend fun sendEvents(events: List<AnalyticsEvent>): Boolean {
        return try {
            // Use retry policy for the single provider
            retryPolicy.execute {
                provider.sendEvents(events)
            }
        } catch (e: Exception) {
            println("NetworkManager error: ${e.message}")
            false
        }
    }

}