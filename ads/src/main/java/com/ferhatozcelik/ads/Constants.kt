package com.ferhatozcelik.ads

import android.content.Context
import android.content.pm.ApplicationInfo

const val BANNER_TEST_ID = "ca-app-pub-3940256099942544/6300978111"
const val INTERSTIALAD_TEST_ID = "ca-app-pub-3940256099942544/1033173712"
fun Context.isDebuggable(): Boolean {
    return 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
}
