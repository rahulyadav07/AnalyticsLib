package com.rahulyadav.analytics.analytics

interface Analytics {
    fun init(token:String?)
    fun logEvent(eventName:String, params:Map<String, Any>)
}