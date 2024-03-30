package com.ferhatozcelik.ads.listener

import com.google.android.gms.ads.initialization.AdapterStatus

interface UmpCallback {
    fun onConsent(message: MutableMap<String, AdapterStatus>)
    fun onFailed(message: String?, errorCode: Int)

}