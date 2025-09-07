package com.rahulyadav.analytics.storage

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsEventDao {
    @Query("SELECT * FROM analytics_events WHERE isSent = 0 ORDER BY timestamp ASC")
    suspend fun getPendingEvents(): List<AnalyticsEventEntity>

    @Query("SELECT * FROM analytics_events ORDER BY timestamp ASC")
    suspend fun getAll(): List<AnalyticsEventEntity>

    @Query("SELECT * FROM analytics_events ORDER BY timestamp ASC")
    fun getAllFlow(): Flow<List<AnalyticsEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<AnalyticsEventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: AnalyticsEventEntity)

    @Query("DELETE FROM analytics_events WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    @Query("DELETE FROM analytics_events")
    suspend fun deleteAll()

    @Query("UPDATE analytics_events SET isSent = 1 WHERE id IN (:ids)")
    suspend fun markAsSent(ids: List<String>)

    @Query("SELECT COUNT(*) FROM analytics_events WHERE isSent = 0")
    suspend fun getPendingEventsCount(): Int
}
