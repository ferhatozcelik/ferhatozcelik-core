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

### Groovy:

```groovy
// https://mvnrepository.com/artifact/com.ferhatozcelik/core
implementation 'com.ferhatozcelik:core:1.0.4'

```kotlin
// https://mvnrepository.com/artifact/com.ferhatozcelik/core
implementation("com.ferhatozcelik:core:1.0.4")

