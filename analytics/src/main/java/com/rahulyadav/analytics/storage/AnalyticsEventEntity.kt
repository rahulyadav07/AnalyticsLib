package com.rahulyadav.analytics.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rahulyadav.analytics.analytics.AnalyticsEvent

@Entity(tableName = "analytics_events")
data class AnalyticsEventEntity(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val params: String?, // JSON string
    val timestamp: Long,
    val isSent: Boolean = false
)

fun AnalyticsEvent.toEntity(): AnalyticsEventEntity {
    return AnalyticsEventEntity(
        name = this.name,
        params = this.params?.let { com.google.gson.Gson().toJson(it) },
        timestamp = this.timestamp,

    )
}

fun AnalyticsEventEntity.toAnalyticsEvent(): AnalyticsEvent {
    return AnalyticsEvent(
        name = this.name,
        params = this.params?.let { com.google.gson.Gson().fromJson(it, Map::class.java) as? Map<String, Any> },
        timestamp = this.timestamp,

    )
}
