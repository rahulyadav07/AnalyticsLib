package com.rahulyadav.analytics.storage

import android.content.Context
import android.preference.PreferenceManager
import androidx.room.Room
import com.rahulyadav.analytics.analytics.AnalyticsEvent

class StorageManagerImp(val context: Context):StorageManager {

        private val database = Room.databaseBuilder(
            context,
            AnalyticsDatabase::class.java,
            "analytics_db"
        ).build()

    override suspend fun persistEvents(events: List<AnalyticsEvent>) {
        database.eventsDao().insertAll(events.map { it.toEntity() })
    }

    override suspend fun loadPersistedEvents(): List<AnalyticsEvent> {
        return database.eventsDao().getAll().map { it.toAnalyticsEvent() }
    }

    override suspend fun clearPersistedEvents() {
        database.eventsDao().deleteAll()
    }

    override fun saveUserProperty(property: UserProperty) {
        database.userPropertiesDao().insert(property.toEntity())
    }

    override fun saveUserId(userId: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString("user_id", userId)
            .apply()
    }
}
