package com.rahulyadav.analytics.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rahulyadav.analytics.analytics.AnalyticsEvent
import com.rahulyadav.analytics.analytics.AnalyticsProvider

import com.rahulyadav.analytics.network.RetrofitAnalyticProvider
import com.rahulyadav.analytics.storage.StorageManager
import com.rahulyadav.analytics.storage.StorageManagerImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnalyticsSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val storageManager: StorageManager = StorageManagerImp(context)
    private val analyticsProvider: AnalyticsProvider = createAnalyticsProvider()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val pendingEvents = storageManager.getPendingEvents()
            
            if (pendingEvents.isEmpty()) {
                return@withContext Result.success()
            }

            val success = analyticsProvider.sendEvents(pendingEvents)
            
            if (success) {
                // Remove successfully sent events from storage
                storageManager.removeEvents(pendingEvents.map { it.id })
                Result.success()
            } else {
                // Retry with exponential backoff
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createAnalyticsProvider(): AnalyticsProvider {
        // Get endpoint and API key from input data or shared preferences
        val endpoint = inputData.getString("endpoint") ?: "https://api.example.com"
        val apiKey = inputData.getString("apiKey")
        
        return RetrofitAnalyticProvider(endpoint, apiKey)
    }
}
