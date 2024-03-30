package com.ferhatozcelik.firebase.config

/**
 * Created by ferhatozcelik on 27.03.2024.
 */

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson

class RemoteConfig {

    private val gson = Gson()

    companion object {
        private var instance: RemoteConfig? = null
        private const val CONFIG_CACHE_EXPIRATION_SECONDS = 3600L
        private const val TAG = "RemoteConfigManager"
        val INSTANCE: RemoteConfig
            get() {
                if (instance == null) {
                    instance = RemoteConfig()
                }
                return instance as RemoteConfig
            }
    }

    private val config: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance().apply {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(CONFIG_CACHE_EXPIRATION_SECONDS).build()
        setConfigSettingsAsync(configSettings)
        fetch(CONFIG_CACHE_EXPIRATION_SECONDS).addOnCompleteListener {
            if (it.isSuccessful) {
                activate().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Configuration Activated")
                    } else {
                        Log.e(TAG, "Failed to activate configuration")
                    }
                }
            }
        }

    }

    fun setDefaults(defaults: Map<String, Any>) {
        config.setDefaultsAsync(defaults)
    }

    inline fun <reified T> getRemoteConfig(param: String): T? = read(param, T::class.java)

    fun <T> read(param: String, returnType: Class<T>): T? {
        if (FirebaseApp.getInstance()== null) {
            Log.e(TAG, "FirebaseApp is not initialized")
            return null
        }
        val value: Any? = when (returnType) {
            String::class.java -> config.getString(param)
            Boolean::class.java -> config.getBoolean(param)
            Long::class.java -> config.getLong(param)
            Int::class.java -> config.getLong(param).toInt()
            Double::class.java -> config.getDouble(param)
            Float::class.java -> config.getDouble(param).toFloat()
            else -> {
                val json = config.getString(param)
                json.takeIf { it.isNotBlank() }?.let { gson.jsonToObjectOrNull(json, returnType) }
            }
        }
        @Suppress("UNCHECKED_CAST") return (value as? T)
    }

    private fun <T> Gson.jsonToObjectOrNull(json: String?, clazz: Class<T>): T? = try {
        fromJson(json, clazz)
    } catch (ignored: Exception) {
        Log.e(TAG, "Failed to parse json: $json", ignored)
        null
    }

}