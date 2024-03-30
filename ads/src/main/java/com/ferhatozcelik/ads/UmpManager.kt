package com.ferhatozcelik.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.ferhatozcelik.ads.listener.UmpCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "UmpManagerModule"

class UmpManagerModule {

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private var tagForUnderAgeOfConsent: Boolean = false
    private var listener: UmpCallback? = null
    private lateinit var consentInformation: ConsentInformation

    companion object {

        private var mInstance: UmpManagerModule? = null
        val instance: UmpManagerModule
            get() {
                if (mInstance == null) {
                    mInstance = UmpManagerModule()
                }

                return mInstance as UmpManagerModule
            }
    }

    fun setTagForUnderAgeOfConsent(tagForUnderAgeOfConsent: Boolean) {
        this.tagForUnderAgeOfConsent = tagForUnderAgeOfConsent
    }

    fun getMobileAdsInitializeCalled(): Boolean {
        return isMobileAdsInitializeCalled.get()
    }

    fun requestConsent(activity: Activity, listenerUmp: UmpCallback) {
        val params =
            ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(tagForUnderAgeOfConsent)
                .build()
        consentInformation =
            UserMessagingPlatform.getConsentInformation(activity.applicationContext)

        consentInformation.requestConsentInfoUpdate(activity, params, {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { loadAndShowError ->
                if (loadAndShowError != null) {
                    Log.w(
                        TAG, java.lang.String.format(
                            "%s: %s", loadAndShowError.errorCode, loadAndShowError.message
                        )
                    )
                    listenerUmp.onFailed(loadAndShowError.message, loadAndShowError.errorCode)
                } else {
                    if (consentInformation.canRequestAds()) {
                        if (isMobileAdsInitializeCalled.get()) {
                            return@loadAndShowConsentFormIfRequired
                        }
                        isMobileAdsInitializeCalled.set(true)
                        MobileAds.initialize(activity.applicationContext) {
                            listenerUmp.onConsent(it.adapterStatusMap)
                        }
                    }
                }
            }
        }, { requestConsentError: FormError ->
            Log.w(
                TAG, String.format(
                    "%s: %s", requestConsentError.errorCode, requestConsentError.message
                )
            )
            listenerUmp.onFailed(requestConsentError.message, requestConsentError.errorCode)
        })

    }

    fun reset(context: Context) {
        if (context.isDebuggable()) {
            consentInformation.reset()
        }
    }

}
