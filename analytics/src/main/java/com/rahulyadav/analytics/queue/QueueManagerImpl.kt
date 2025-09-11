package com.rahulyadav.analytics.queue

import android.content.Context
import com.rahulyadav.analytics.analytics.AnalyticsConfig
import com.rahulyadav.analytics.analytics.AnalyticsEvent
import com.rahulyadav.analytics.network.NetworkManager
import com.rahulyadav.analytics.network.NetworkManagerImp
import com.rahulyadav.analytics.storage.StorageManager
import com.rahulyadav.analytics.storage.StorageManagerImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

class QueueManagerImpl(private val context: Context,
                       private val config: AnalyticsConfig
) :QueueManager{

    private val eventQueue = ConcurrentLinkedQueue<AnalyticsEvent>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val storageManager: StorageManager = StorageManagerImp(context, config)
    private val networkManager: NetworkManager = NetworkManagerImp(config)



    override fun enqueue(event: AnalyticsEvent) {
        if (eventQueue.size >= config.maxQueueSize) {
            eventQueue.poll() // Remove oldest event if queue is full
        }
        eventQueue.offer(event)

        // Store event locally first
        coroutineScope.launch {
            storageManager.persistEvents(listOf(event))
        }

        // If batch size is reached, send immediately via API (app is running)
        if (eventQueue.size >= config.batchSize!!) {
            flush()
        }
    }

    override fun flush() {
        coroutineScope.launch {
            val events = mutableListOf<AnalyticsEvent>()
            while (events.size < config.batchSize!! && !eventQueue.isEmpty()) {
                eventQueue.poll()?.let { events.add(it) }
            }

            if (events.isNotEmpty()) {
                try {
                    // Try to send via network immediately (app is running)
                    val success = networkManager.sendEvents(events)
                    if (success) {
                        // Remove successfully sent events from local storage
                        storageManager.removeEvents(events.map { it.id })
                    } else {
                        // If failed, events are already stored locally, WorkManager will handle retry
                        println("Failed to send events immediately, WorkManager will retry")
                    }
                } catch (e: Exception) {
                    // Events are already stored locally, WorkManager will handle retry
                    println("Exception sending events immediately: ${e.message}, WorkManager will retry")
                }
            }
        }
    }
}
