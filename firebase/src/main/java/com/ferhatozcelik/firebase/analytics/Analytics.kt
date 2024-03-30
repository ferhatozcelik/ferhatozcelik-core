package com.ferhatozcelik.firebase.analytics

/**
 * Created by ferhatozcelik on 27.03.2024.
 */

import android.content.Context
import android.os.Bundle
import androidx.annotation.Keep
import androidx.annotation.Size
import com.google.firebase.analytics.FirebaseAnalytics

@Keep
class Analytics(context: Context) {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    /**
     * Command to Enable Analytics in Debug Mode
     * Command 1: adb shell setprop debug.firebase.analytics.app packageName
     * Command 2: adb shell setprop log.tag.FA VERBOSE
     * Command 3: adb shell setprop log.tag.FA-SVC VERBOSE
     * Command 4: adb logcat -v time -s FA FA-SVC
     * */

    fun sendEventAnalytics(@Size(min = 1L, max = 40L) eventName: String, eventStatus: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.VALUE, eventStatus)
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun sendTrackedScreenAnalytics(@Size(min = 1L, max = 40L) eventName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, eventName)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun sendCpmEventAnalytics(@Size(min = 1L, max = 40L) revenue: Double, currency: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency)
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, revenue)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle)

    }

}