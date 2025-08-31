package com.rahulyadav.analytics.session

interface SessionManager {

    fun startSession()
    fun endSession()
    fun getCurrentSessionId(): String?

}

