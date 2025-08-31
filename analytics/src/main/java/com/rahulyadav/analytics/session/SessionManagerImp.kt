package com.rahulyadav.analytics.session

import java.util.UUID

class SessionManagerImp:SessionManager {
    private var currentSessionId: String? = null
    private var sessionStartTime: Long = 0

    override fun startSession() {
        currentSessionId = UUID.randomUUID().toString()
        sessionStartTime = System.currentTimeMillis()

    }

    override fun endSession() {
        currentSessionId = null

    }

    override fun getCurrentSessionId(): String?  = currentSessionId


}