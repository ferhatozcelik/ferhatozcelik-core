package com.ferhatozcelik.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ferhatozcelik.firebase.analytics.Analytics
import com.ferhatozcelik.firebase.config.UpdateManager
import com.ferhatozcelik.firebase.config.RemoteConfig
import com.ferhatozcelik.firebase.fcm.FirebaseFCM
import com.ferhatozcelik.firebase.model.Channel


class MainActivity : AppCompatActivity() {

    private val customKey = "custom_key"
    private val defaultValue = "Custom Value"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Analytics(this).sendEventAnalytics("MainActivity", "onCreate")

       val testAPram = RemoteConfig.INSTANCE.getRemoteConfig<String>("custom_key")

        val forceUpdate = UpdateManager.getIsForceUpdate()
        val forceUpdateVersion = UpdateManager.getForceUpdateVersion()


        FirebaseFCM.instance.initializePushNotification(this, R.drawable.ic_launcher_foreground, listOf(
            Channel("test", "Test Channel"),
            Channel("test2", "Test Channel 2")

        ))

        FirebaseFCM.instance.subscribeToTopic("test")
        FirebaseFCM.instance.removeFCMTopic("test")

    }


}