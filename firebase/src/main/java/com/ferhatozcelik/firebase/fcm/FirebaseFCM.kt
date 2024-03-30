package com.ferhatozcelik.firebase.fcm

/**
 * Created by ferhatozcelik on 27.03.2024.
 */

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ferhatozcelik.firebase.model.Channel
import com.ferhatozcelik.firebase.model.PushNotification
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

@Keep
class FirebaseFCM {

    private var pushNotificationConfig = PushNotification()

    companion object {
        private var mInstance: FirebaseFCM? = null

        val instance: FirebaseFCM
            get() {
                if (mInstance == null) {
                    mInstance = FirebaseFCM()
                }
                return mInstance as FirebaseFCM
            }
    }

    fun initializePushNotification(
        activity: Activity,
        @DrawableRes smallIcon: Int,
        channelList: List<Channel>
    ) {
        try {
            FirebaseApp.initializeApp(activity.applicationContext)
            askNotificationPermission(activity)
            channelList.forEach {
                createNotificationChannel(activity.applicationContext, it.id, it.name)
            }
            this.pushNotificationConfig = PushNotification(
                smallIcon = smallIcon,
                channelList = channelList
            )
        } catch (e: Exception) {
            Log.i(TAG, e.message.toString())
        }
    }

    fun getNotificationConfig(): PushNotification {
        return this.pushNotificationConfig
    }

    fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    fun removeFCMTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }

    private fun askNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity.applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1000)
            }
        }
    }

    private fun createNotificationChannel(context: Context, channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }
}