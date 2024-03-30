package com.ferhatozcelik.firebase.fcm

/**
 * Created by ferhatozcelik on 27.03.2024.
 */

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.core.app.NotificationCompat
import com.ferhatozcelik.firebase.R
import com.ferhatozcelik.firebase.analytics.Analytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

internal const val TAG = "FCMService"

@Keep
class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i(TAG, "onMessageReceived: ")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            val channelId = remoteMessage.data["channelId"]

            if (title.isNullOrEmpty() || body.isNullOrEmpty()) {
                return
            } else {
                val notificationConfig = FirebaseFCM.instance.getNotificationConfig()
                Handler(this.mainLooper).post {
                    if (notificationConfig.channelList.isEmpty().not() && channelId.isNullOrEmpty().not()) {
                        notificationConfig.channelList.forEach {
                            if (it.id == channelId) {
                                sendNotification(
                                    title, body, it.id, it.name, notificationConfig.smallIcon, packageName
                                )
                            }
                        }
                    } else {
                        sendNotification(
                            title, body, getString(R.string.default_notification_channel), getString(R.string.default_notification), notificationConfig.smallIcon, packageName
                        )
                    }
                }
            }
        }

        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message data payload: ${remoteMessage.notification}")

            val channelId = remoteMessage.notification?.channelId
            val title = remoteMessage.notification?.title
            val body = remoteMessage.notification?.body

            if (title.isNullOrEmpty() || body.isNullOrEmpty()) {
                return
            } else {
                val notificationConfig = FirebaseFCM.instance.getNotificationConfig()
                Handler(this.mainLooper).post {

                    if (notificationConfig.channelList.isEmpty().not() && channelId.isNullOrEmpty().not()) {
                        notificationConfig.channelList.forEach {
                            if (it.id == channelId) {
                                sendNotification(
                                    title, body, it.id, it.name, notificationConfig.smallIcon, packageName
                                )
                            }
                        }
                    } else {
                        sendNotification(
                            title, body, getString(R.string.default_notification_channel), getString(R.string.default_notification), notificationConfig.smallIcon, packageName
                        )
                    }
                }
            }
        }

    }

    private fun sendNotification(
        messageTitle: String, messageBody: String, channelId: String, channelName: String, @DrawableRes smallIcon: Int?, storePackage: String
    ) {
        Analytics(this).sendEventAnalytics("push_notification", "push_notification_received")

        val alreadyInstalled = isAppInstalled(storePackage)
        val intent = if (alreadyInstalled) {
            packageManager.getLaunchIntentForPackage(storePackage)
        } else try {
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$storePackage"))
        } catch (e: ActivityNotFoundException) {
            Intent(
                Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$storePackage")
            )
        }

        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId).setContentTitle(messageTitle).setContentText(messageBody).setAutoCancel(true).setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        smallIcon?.let {
            notificationBuilder.setSmallIcon(it)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            applicationInfo.enabled
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

}