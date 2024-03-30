# Core Library

[![Core](https://img.shields.io/maven-central/v/com.ferhatozcelik/core.svg)](https://search.maven.org/artifact/com.ferhatozcelik/core)

The Core Library provides essential utilities and extensions to simplify Android app development.

## Features:

- **Extensions**: Provides extensions for common Android components to reduce boilerplate code.
  - `ActivityExtensions`: Extensions related to activities.
  - `ClickListeners.kt`: Extensions for click listeners.
  - `ContextExtensions.kt`: Extensions related to context.
  - `EditTextExtensions.kt`: Extensions for EditText.
  - `FragmentExtensions.kt`: Extensions for fragments.
  - `IntentExtensions.kt`: Extensions for intents.
  - `StringExtension.kt`: Extensions for strings.
  - `ValidationExtensions.kt`: Extensions for validation.
  - `ViewExtensions.kt`: Extensions for views.

- **In-App Update**: Integrates Google Play Core libraries' in-app updates feature to prompt active users to update the app within the app itself.
In-App Update:
When your users keep your app up to date on their devices, they can try new features, as well as benefit from performance improvements and bug fixes. Although some users enable background updates when their device is connected to an unmetered connection, other users might need to be reminded to install updates. In-app updates is a Google Play Core libraries feature that prompts active users to update your app.
For more information on how to integrate in-app updates into your app, refer to the official documentation.
https://developer.android.com/guide/playcore/in-app-updates

## Installation:

You can include the Core Library in your project using Gradle.

```groovy

implementation 'com.ferhatozcelik:core:latest'

implementation("com.ferhatozcelik:core:latest")

```

# Firebase Integration for Android

[![Firebase](https://img.shields.io/maven-central/v/com.ferhatozcelik/core.svg)](https://search.maven.org/artifact/com.ferhatozcelik/firebase)


## Installation:

You can include the Firebase Library in your project using Gradle.

```groovy

implementation 'com.ferhatozcelik:firebase:latest'

implementation("com.ferhatozcelik:firebase:latest")

```

This guide provides instructions on how to integrate Firebase Analytics, Remote Config, and Push Notification features into your Android app.

## Firebase Analytics

Firebase Analytics allows you to track user interactions with your app and provides insights into user behavior. You can track various events such as user actions, screen views, and more.

### Usage:

To send an event analytics:

```kotlin
Analytics(this).sendEventAnalytics("MainActivity", "onCreate")

```

### Firebase Remote Config
Firebase Remote Config enables you to dynamically change the behavior and appearance of your app without publishing an app update. You can define parameters and their default values in the Firebase console, and then fetch and activate them in your app.

### Usage:
To fetch a remote config parameter:

```kotlin

val testParam = RemoteConfig.INSTANCE.getRemoteConfig<String>("custom_key")

```

### Firebase Push Notification
Firebase Push Notification allows you to send notifications to your app users. You can target specific devices or user segments and send personalized notifications.

### Usage:
To initialize push notifications:

```kotlin

FirebaseFCM.instance.initializePushNotification(
    context,
    R.drawable.ic_launcher_foreground,
    listOf(
        Channel("test", "Test Channel"),
        Channel("test2", "Test Channel 2")
    )
)

```

### To subscribe to a topic:

```kotlin

FirebaseFCM.instance.subscribeToTopic("test")

```

### To remove a topic subscription:

```kotlin

FirebaseFCM.instance.removeFCMTopic("test")

```

### Getting Started:
Add the Firebase SDK to your project.
Follow the Firebase documentation to set up Firebase Analytics, Remote Config, and Push Notification services in your project.
Use the provided code snippets to implement the desired functionality in your app.

https://firebase.google.com/docs/analytics/get-started
https://firebase.google.com/docs/remote-config
https://firebase.google.com/docs/cloud-messaging

[![IOTl](https://img.shields.io/maven-central/v/com.ferhatozcelik/core.svg)](https://search.maven.org/artifact/com.ferhatozcelik/iot)

### IOT Library for Android Things
The IOT Library for Android Things provides utilities for managing IOT devices, particularly ESP8266-based devices, in Android Things projects.

# ESP8266 and ESP32 Libraries for Arduino

These libraries provide utilities for working with ESP8266 and ESP32 microcontrollers using the Arduino framework.

## Overview

The ESP8266 and ESP32 libraries offer a range of functionalities to simplify development for these popular microcontrollers. Whether you're working with Wi-Fi connectivity, GPIO control, or sensor interfacing, these libraries aim to streamline your development process.

This library simplifies the integration of ESP8266-based devices into your Android Things project, allowing you to manage Wi-Fi connections, retrieve sensor data, and handle various states.

## Images

![ESP32 Dev Board](https://upload.wikimedia.org/wikipedia/commons/2/20/ESP32_Espressif_ESP-WROOM-32_Dev_Board.jpg)
![ESP8266 Mounted on Adapter](https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/ESP8266_mounted_on_adapter.jpg/330px-ESP8266_mounted_on_adapter.jpg)


### Initialization
```kotlin

private lateinit var eps8266WifiManager: Esp8266WifiManager
private lateinit var esp8266DataManager: Esp8266DataManager

// Initialize EPS8266WifiManager and Esp8266DataManager
eps8266WifiManager = Esp8266WifiManager.getInstance(requireContext())
eps8266WifiManager.setWifiStateListener(setWifiStateListener)

esp8266DataManager = Esp8266DataManager.getInstance(requireContext())
esp8266DataManager.setWifiDataListener(wifiDataListener)

```

### Connection Handling
```kotlin

override fun onStop() {
    super.onStop()
    if (eps8266WifiManager.isWifiConnected()) {
        eps8266WifiManager.disconnect()
    }
    esp8266DataManager.stopReadingData()
}

// Connect to Wi-Fi and start reading data
if (!eps8266WifiManager.isWifiConnected()) {
    eps8266WifiManager.connectToWifi(SSID, PASSWORD)
} else {
    esp8266DataManager.startReadingData()
}
```
### Callbacks
```kotlin

private val setWifiStateListener = object : WifiStateListener {
    override fun onWifiConnecting() {
        // Handle Wi-Fi connecting state
    }

    override fun onWifiConnected() {
        // Handle Wi-Fi connected state
    }

    override fun onWifiDisconnected() {
        // Handle Wi-Fi disconnected state
    }
}


private val wifiDataListener = object : WifiDataListener {
    override fun onWifiDataReceived(data: Any) {
        // Handle received Wi-Fi data
    }

    override fun onWifiDataError(error: String) {
        // Handle Wi-Fi data error
    }
}
```
