package com.ferhatozcelik.firebase.analytics.compose

/**
 * Created by ferhatozcelik on 27.03.2024.
 */

import android.os.Bundle
import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

@Keep
@Composable
fun SendTrackedScreenAnalytics(
    eventName: String,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    DisposableEffect(lifecycleOwner) {
        val observer: LifecycleEventObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                    param(FirebaseAnalytics.Param.SCREEN_NAME, eventName)
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Keep
@Composable
fun SendEventAnalytics(
    eventName: String,
    eventStatus: String,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.VALUE, eventStatus)
                Firebase.analytics.logEvent(eventName, bundle)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Keep
@Composable
fun SendCpmEventAnalytics(
    currency: String,
    revenue: Double,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency)
                bundle.putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}