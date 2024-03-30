package com.ferhatozcelik.ads.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.ferhatozcelik.ads.BANNER_TEST_ID
import com.ferhatozcelik.ads.UmpManagerModule
import com.ferhatozcelik.ads.isDebuggable
import com.ferhatozcelik.firebase.analytics.Analytics
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAdView(adUnistId: String = BANNER_TEST_ID) {

    val isMobileAdsInitializeCalled = UmpManagerModule.instance.getMobileAdsInitializeCalled()
    if (isMobileAdsInitializeCalled) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.FULL_BANNER)
                    adUnitId = if (context.isDebuggable()) BANNER_TEST_ID else adUnistId
                    loadAd(AdRequest.Builder().build())
                    adListener = object : com.google.android.gms.ads.AdListener() {
                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            Analytics(context.applicationContext)
                                .sendEventAnalytics("BannerAdView", "onAdLoaded")
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            Analytics(context.applicationContext)
                                .sendEventAnalytics("BannerAdView", "onAdClicked")
                        }

                        override fun onAdClosed() {
                            super.onAdClosed()
                            Analytics(context.applicationContext)
                                .sendEventAnalytics("BannerAdView", "onAdClosed")
                        }

                        override fun onAdOpened() {
                            super.onAdOpened()
                            Analytics(context.applicationContext)
                                .sendEventAnalytics("BannerAdView", "onAdOpened")
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                            Analytics(context.applicationContext)
                                .sendEventAnalytics("BannerAdView", "onAdImpression")
                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            Analytics(context.applicationContext)
                                .sendEventAnalytics("BannerAdView", "onAdFailedToLoad")
                        }
                    }
                }
            }
        )

    }

}


