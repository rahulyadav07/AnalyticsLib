package com.rahulyadav.analytics.analytics

import android.content.Context
import com.rahulyadav.analytics.UserProperty

import com.rahulyadav.analytics.network.NetworkManager
import com.rahulyadav.analytics.network.NetworkManagerImp
import com.rahulyadav.analytics.privacy.PrivacyManageImp
import com.rahulyadav.analytics.privacy.PrivacyManager
import com.rahulyadav.analytics.queue.QueueManager
import com.rahulyadav.analytics.queue.QueueManagerImpl
import com.rahulyadav.analytics.session.SessionManager
import com.rahulyadav.analytics.session.SessionManagerImp
import com.rahulyadav.analytics.storage.StorageManager
import com.rahulyadav.analytics.storage.StorageManagerImp

class AnalyticImp private constructor( private val context: Context, private val config: AnalyticsConfig) : Analytics {
    private val queueManager: QueueManager = QueueManagerImpl(context, config)
    private val sessionManager: SessionManager = SessionManagerImp()
    private val storageManager: StorageManager = StorageManagerImp(context)
    private val networkManager: NetworkManager = NetworkManagerImp(config)
    private val privacyManager: PrivacyManager = PrivacyManageImp(config.privacySettings)

    private var userId: String? = null
    private val deviceInfo = DeviceInfo(context)

    companion object {
        @Volatile private var instance: AnalyticImp? = null

        fun init(context: Context, config: AnalyticsConfig) {
            instance ?: synchronized(this) {
                instance ?: AnalyticImp(context, config).also { instance = it }
            }
        }

        fun getInstance(): AnalyticImp =
            instance ?: throw IllegalStateException("Analytics must be initialized first")
    }

    override fun init(token: String?) {

    }

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        val event = AnalyticsEvent(
            name = eventName,
            params = params,
            sessionId = sessionManager.getCurrentSessionId(),
            userId = userId,
            deviceInfo = deviceInfo
        )

        queueManager.enqueue(event)
    }

    override fun setUserProperty(property: String, value: String) {
        val userProperty = UserProperty(property, value, userId)
        storageManager.saveUserProperty(userProperty)
    }

    override fun setUserId(userId: String) {
        this.userId = userId
        storageManager.saveUserId(userId)
    }

    override fun startSession() {
        sessionManager.startSession()
    }

    override fun endSession() {
        sessionManager.endSession()
    }
}
