package com.ferhatozcelik.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ferhatozcelik.firebase.analytics.Analytics


class MainActivity : AppCompatActivity() {

    private val customKey = "custom_key"
    private val defaultValue = "Custom Value"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Analytics(this).sendEventAnalytics("MainActivity", "onCreate")


    }

}