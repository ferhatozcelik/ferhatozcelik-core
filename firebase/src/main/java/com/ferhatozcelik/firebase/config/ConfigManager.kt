package com.ferhatozcelik.firebase.config

/**
 * Created by ferhatozcelik on 27.03.2024.
 */

class ConfigManager {

    private val remoteConfigManager = RemoteConfigManager

    companion object {
        const val DEFAULT_FORCE_UPDATE = true
        const val DEFAULT_FORCE_UPDATE_VERSION = "1.0.0"

    }

    fun getIsForceUpdate(): Boolean = remoteConfigManager.getIsForceUpdate()

    fun getForceUpdateVersion(): String = remoteConfigManager.getForceUpdateVersion()

}
