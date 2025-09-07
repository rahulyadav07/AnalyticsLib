package com.rahulyadav.analytics.sync

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class AnalyticsWorkManager(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    companion object {
        private const val SYNC_WORK_NAME = "analytics_sync_work"
        private const val PERIODIC_SYNC_WORK_NAME = "analytics_periodic_sync_work"
        private const val MIN_BACKOFF_MILLIS = 10000L // 10 seconds
    }

    fun scheduleImmediateSync(endpoint: String? = null, apiKey: String? = null) {
        val inputData = Data.Builder()
            .putString("endpoint", endpoint)
            .putString("apiKey", apiKey)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<AnalyticsSyncWorker>()
            .setInputData(inputData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    fun schedulePeriodicSync(intervalMinutes: Long = 15, endpoint: String? = null, apiKey: String? = null) {
        val inputData = Data.Builder()
            .putString("endpoint", endpoint)
            .putString("apiKey", apiKey)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<AnalyticsSyncWorker>(
            intervalMinutes, TimeUnit.MINUTES
        )
            .setInputData(inputData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }

    fun cancelAllSyncWork() {
        workManager.cancelUniqueWork(SYNC_WORK_NAME)
        workManager.cancelUniqueWork(PERIODIC_SYNC_WORK_NAME)
    }

    fun getWorkInfo(): List<WorkInfo> {
        return workManager.getWorkInfosForUniqueWork(SYNC_WORK_NAME).get()
    }
}