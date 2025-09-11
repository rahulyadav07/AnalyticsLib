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

    private val storageManager: StorageManager = StorageManagerImp(context, createDefaultConfig())
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
        // API key and endpoint are hardcoded in RetrofitAnalyticProvider
        return RetrofitAnalyticProvider()
    }
    
    private fun createDefaultConfig(): com.rahulyadav.analytics.analytics.AnalyticsConfig {
        return com.rahulyadav.analytics.analytics.AnalyticsConfig(
            maxDatabaseSize = 10000  // Default database size limit
        )
    }
}
