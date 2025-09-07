package com.rahulyadav.analytics.storage

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [AnalyticsEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AnalyticsDatabase : RoomDatabase() {
    abstract fun eventsDao(): AnalyticsEventDao
}
