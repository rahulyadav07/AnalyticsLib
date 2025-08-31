package com.rahulyadav.analytics.network

import com.rahulyadav.analytics.analytics.AnalyticsEvent
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AnalyticsApiService {
    @POST("analytics/events")
    suspend fun sendEvents(
        @Header("Authorization") authorization: String? = null,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("User-Agent") userAgent: String = "RahulAnalyticsSDK/1.0",
        @Body request:  EventsBatchRequest
    ): Response<JSONObject>
}

// Request/Response Models
data class EventsBatchRequest(
    val events: List<AnalyticsEvent>,
    val batchId: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val sdkVersion: String = "1.0.0"
)
