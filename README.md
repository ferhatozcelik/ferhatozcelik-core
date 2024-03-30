# Core Library

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

implementation 'com.ferhatozcelik:core:1.0.4'

implementation("com.ferhatozcelik:core:1.0.4")

# Firebase Integration for Android

This guide provides instructions on how to integrate Firebase Analytics, Remote Config, and Push Notification features into your Android app.

## Firebase Analytics

Firebase Analytics allows you to track user interactions with your app and provides insights into user behavior. You can track various events such as user actions, screen views, and more.

### Usage:

To send an event analytics:

```kotlin
Analytics(this).sendEventAnalytics("MainActivity", "onCreate")

Firebase Remote Config
Firebase Remote Config enables you to dynamically change the behavior and appearance of your app without publishing an app update. You can define parameters and their default values in the Firebase console, and then fetch and activate them in your app.

Usage:
To fetch a remote config parameter:

val testParam = RemoteConfig.INSTANCE.getRemoteConfig<String>("custom_key")

Firebase Push Notification
Firebase Push Notification allows you to send notifications to your app users. You can target specific devices or user segments and send personalized notifications.

Usage:
To initialize push notifications:

FirebaseFCM.instance.initializePushNotification(
    context,
    R.drawable.ic_launcher_foreground,
    listOf(
        Channel("test", "Test Channel"),
        Channel("test2", "Test Channel 2")
    )
)

To subscribe to a topic:

FirebaseFCM.instance.subscribeToTopic("test")

To remove a topic subscription:

FirebaseFCM.instance.removeFCMTopic("test")

Getting Started:
Add the Firebase SDK to your project.
Follow the Firebase documentation to set up Firebase Analytics, Remote Config, and Push Notification services in your project.
Use the provided code snippets to implement the desired functionality in your app.
https://firebase.google.com/docs/analytics/get-started?hl=tr&platform=web
https://firebase.google.com/docs/remote-config?hl=tr
https://firebase.google.com/docs/cloud-messaging?hl=tr










