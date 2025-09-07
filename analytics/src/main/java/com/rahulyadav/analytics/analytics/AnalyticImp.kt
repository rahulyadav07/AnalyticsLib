package com.rahulyadav.analytics.analytics

 import android.content.Context
import com.rahulyadav.analytics.queue.QueueManager
import com.rahulyadav.analytics.queue.QueueManagerImpl
import com.rahulyadav.analytics.sync.AnalyticsWorkManager


class AnalyticImp private constructor( context: Context, private val config: AnalyticsConfig) : Analytics {
    private val queueManager: QueueManager = QueueManagerImpl(context, config)
    private val workManager: AnalyticsWorkManager = AnalyticsWorkManager(context)

    init {
        // Auto-initialize WorkManager when AnalyticImp is created
        workManager.schedulePeriodicSync()
        
        // Also trigger immediate sync on app start (in case periodic didn't run)
        workManager.scheduleImmediateSync()
    }

    companion object {
        @Volatile private var instance: AnalyticImp? = null

        fun init(context: Context, config: AnalyticsConfig) {
            instance ?: synchronized(this) {
                instance ?: AnalyticImp(context.applicationContext, config).also { instance = it }
            }
        }

        fun getInstance(): AnalyticImp =
            instance ?: throw IllegalStateException("Analytics must be initialized first")

        // Static convenience methods
        fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
            getInstance().logEvent(eventName, params)
        }

    }

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        val event = AnalyticsEvent(
            name = eventName,
            params = params,
            userId = null
        )

        queueManager.enqueue(event)
    }
}
