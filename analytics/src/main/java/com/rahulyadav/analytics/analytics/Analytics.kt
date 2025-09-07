package com.rahulyadav.analytics.analytics

interface Analytics {
    fun logEvent(eventName:String, params:Map<String, Any>)
}