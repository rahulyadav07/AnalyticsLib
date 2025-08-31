package com.rahulyadav.analytics.network

import com.rahulyadav.analytics.analytics.AnalyticsConfig
import com.rahulyadav.analytics.analytics.AnalyticsEvent

class NetworkManagerImp(private val config:AnalyticsConfig):NetworkManager {

    private val retryPolicy = ExponentialBackoffRetry(config.maxRetryAttempts)

    override suspend fun sendEvents(events: List<AnalyticsEvent>) {
        config.providers.forEach { provider ->
            retryPolicy.execute {
                provider.sendEvents(events)
            }
        }
    }

}