package com.rahulyadav.analytics.queue

import com.rahulyadav.analytics.analytics.AnalyticsEvent

interface QueueManager {
    fun enqueue(event: AnalyticsEvent)
    fun flush()

}