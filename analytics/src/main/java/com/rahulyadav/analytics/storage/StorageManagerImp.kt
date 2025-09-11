package com.rahulyadav.analytics.storage

import android.content.Context
import androidx.room.Room
import com.rahulyadav.analytics.analytics.AnalyticsEvent

class StorageManagerImp(val context: Context, private val config: com.rahulyadav.analytics.analytics.AnalyticsConfig):StorageManager {

        private val database = Room.databaseBuilder(
            context,
            AnalyticsDatabase::class.java,
            "analytics_db"
        ).build()

    override suspend fun persistEvents(events: List<AnalyticsEvent>) {
        // Insert new events
        database.eventsDao().insertAll(events.map { it.toEntity() })
        
        // Apply eviction strategy if database is over limit
        evictOldEventsIfNeeded()
    }
    
    private suspend fun evictOldEventsIfNeeded() {
        val currentCount = database.eventsDao().getCount()
        if (currentCount > config.maxDatabaseSize) {
            val eventsToDelete = currentCount - config.maxDatabaseSize
            println("Analytics: Database has $currentCount events, evicting $eventsToDelete oldest events")
            
            // Delete oldest events (FIFO - First In, First Out)
            database.eventsDao().deleteOldestEvents(eventsToDelete)
        }
    }

    override suspend fun loadPersistedEvents(): List<AnalyticsEvent> {
        return database.eventsDao().getAll().map { it.toAnalyticsEvent() }
    }

    override suspend fun clearPersistedEvents() {
        database.eventsDao().deleteAll()
    }


    override suspend fun getPendingEvents(): List<AnalyticsEvent> {
        return database.eventsDao().getPendingEvents().map { it.toAnalyticsEvent() }
    }

    override suspend fun removeEvents(eventIds: List<String>) {
        database.eventsDao().deleteByIds(eventIds)
    }
}
