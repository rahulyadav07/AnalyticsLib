package com.rahulyadav.analytics.queue

import android.content.Context
import android.os.storage.StorageManager
import com.rahulyadav.analytics.analytics.AnalyticsConfig
import com.rahulyadav.analytics.analytics.AnalyticsEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

class QueueManagerImpl(private val context: Context,
                       private val config: AnalyticsConfig
) :QueueManager{

    private val eventQueue = ConcurrentLinkedQueue<AnalyticsEvent>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val storageManager: StorageManager = StorageManagerImpl(context)
    private val networkManager: NetworkManager = NetworkManagerImpl(config)
    init {
        startBatchProcessor()
        loadPersistedEvents()
    }


    override fun enqueue(event: AnalyticsEvent) {
        if (eventQueue.size >= config.maxQueueSize) {
            eventQueue.poll() // Remove oldest event if queue is full
        }
        eventQueue.offer(event)

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
                    networkManager.sendEvents(events)
                } catch (e: Exception) {
                    storageManager.persistEvents(events)
                }
            }
        }
    }

    private fun startBatchProcessor() {
        coroutineScope.launch {
            while (isActive) {
                delay(config.batchIntervalMs)
                flush()
            }
        }
    }

    private fun loadPersistedEvents() {
        coroutineScope.launch {
            val persistedEvents = storageManager.loadPersistedEvents()
            persistedEvents.forEach { enqueue(it) }
            storageManager.clearPersistedEvents()
        }
    }
}
