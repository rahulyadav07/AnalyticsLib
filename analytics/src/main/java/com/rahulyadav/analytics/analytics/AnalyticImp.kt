package com.rahulyadav.analytics.analytics

 import android.content.Context
import com.rahulyadav.analytics.queue.QueueManager
import com.rahulyadav.analytics.queue.QueueManagerImpl
import com.rahulyadav.analytics.sync.AnalyticsWorkManager

class AnalyticImp private constructor( private val context: Context, private val config: AnalyticsConfig) : Analytics {
    private val queueManager: QueueManager = QueueManagerImpl(context, config)
    private val workManager: AnalyticsWorkManager = AnalyticsWorkManager(context)

    private val deviceInfo = DeviceInfo(
        version = android.os.Build.VERSION.RELEASE,
        model = android.os.Build.MODEL,
        manufacturer = android.os.Build.MANUFACTURER,
        osVersion = android.os.Build.VERSION.SDK_INT.toString()
    )

    companion object {
        @Volatile private var instance: AnalyticImp? = null

        fun init(context: Context, config: AnalyticsConfig) {
            instance ?: synchronized(this) {
                instance ?: AnalyticImp(context, config).also { instance = it }
            }
        }

        fun getInstance(): AnalyticImp =
            instance ?: throw IllegalStateException("Analytics must be initialized first")

        // Static convenience methods
        fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
            getInstance().logEvent(eventName, params)
        }

        fun init(token: String?) {
            getInstance().init(token)
        }
    }

    override fun init(token: String?) {
        // Initialize periodic sync with config providers (only for when app is not running)
        val endpoint = config.providers.firstOrNull()?.let { 
            if (it is com.rahulyadav.analytics.network.RetrofitAnalyticProvider) {
                // Extract endpoint from provider - this is a simplified approach
                "https://api.example.com" // You should store this in config
            } else null
        }
        workManager.schedulePeriodicSync(endpoint = endpoint, apiKey = token)
    }

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        val event = AnalyticsEvent(
            name = eventName,
            params = params,
            userId = null,
            deviceInfo = deviceInfo
        )

        queueManager.enqueue(event)
    }
}
