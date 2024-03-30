package com.ferhatozcelik.iot.esp32.bluetooth.util

import android.content.Context

enum class Connected {
    False,
    Pending,
    True
}

object Constants {

    fun getApplicationId(context: Context): String {
        return context.packageName
    }

    fun getIntentActionDisconnect(context: Context): String {
        return getApplicationId(context) + ".Disconnect"
    }

    fun getNotificationChannel(context: Context): String {
        return getApplicationId(context) + ".Channel"
    }

    fun getIntentClassMainActivity(context: Context): String {
        return getApplicationId(context) + ".MainActivity"
    }

    const val NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001
}