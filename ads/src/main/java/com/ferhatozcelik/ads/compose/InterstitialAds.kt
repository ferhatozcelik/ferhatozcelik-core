package com.ferhatozcelik.ads.compose

import android.util.Log
import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.ferhatozcelik.ads.INTERSTIALAD_TEST_ID
import com.ferhatozcelik.ads.UmpManagerModule
import com.ferhatozcelik.ads.isDebuggable
import com.ferhatozcelik.core.extensions.getActivity
import com.ferhatozcelik.firebase.analytics.Analytics
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

const val TAG = "InterstitialAds"

@Keep
@Composable
fun ShowInterstialAd(
    adUnitId: String = INTERSTIALAD_TEST_ID,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                val isMobileAdsInitializeCalled = UmpManagerModule.instance.getMobileAdsInitializeCalled()
                if (isMobileAdsInitializeCalled) {

                    val adRequest = AdRequest.Builder().build()

                    InterstitialAd.load(
                        context,
                        if (context.isDebuggable()) INTERSTIALAD_TEST_ID else adUnitId,
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                super.onAdLoaded(interstitialAd)
                                context.getActivity().let {
                                    interstitialAd.show(it)
                                }
                                Analytics(context.applicationContext)
                                    .sendEventAnalytics("InterstitialAd", "onAdLoaded")

                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                super.onAdFailedToLoad(loadAdError)
                                Log.d(TAG, loadAdError.toString())
                                Analytics(context.applicationContext)
                                    .sendEventAnalytics("InterstitialAd", "onAdFailedToLoad")
                            }

                        })
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
