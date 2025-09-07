package com.rahulyadav.analytics.network

import com.rahulyadav.analytics.ExponentialBackoffRetry
import com.rahulyadav.analytics.analytics.AnalyticsConfig
import com.rahulyadav.analytics.analytics.AnalyticsEvent

class NetworkManagerImp(private val config:AnalyticsConfig):NetworkManager {

    private val retryPolicy = ExponentialBackoffRetry(config.maxRetryAttempts)

    override suspend fun sendEvents(events: List<AnalyticsEvent>): Boolean {
        return try {
            var allSuccess = true
            config.providers.forEach { provider ->
                val success = provider.sendEvents(events)
                if (!success) {
                    allSuccess = false
                }
            }
            allSuccess
        } catch (e: Exception) {
            println("NetworkManager error: ${e.message}")
            false
        }
    }

}