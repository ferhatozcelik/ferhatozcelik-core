package com.ferhatozcelik.firebase.config

/**
 * Created by ferhatozcelik on 27.03.2024.
 */

object RemoteConfigManager {

    private val remoteConfigApi = RemoteConfig.INSTANCE

    init {
        setDefaults()
    }

    private fun setDefaults() {
        remoteConfigApi.setDefaults(
            mapOf(
                RemoteConfigParam.FORCE_UPDATE.key to UpdateManager.DEFAULT_FORCE_UPDATE,
                RemoteConfigParam.FORCE_UPDATE_VERSION.key to UpdateManager.DEFAULT_FORCE_UPDATE_VERSION
            )
        )

    }

    fun getIsForceUpdate(): Boolean =
        remoteConfigApi.getRemoteConfig<Boolean>(RemoteConfigParam.FORCE_UPDATE.key)
            ?: UpdateManager.DEFAULT_FORCE_UPDATE

    fun getForceUpdateVersion(): String =
        remoteConfigApi.getRemoteConfig<String>(RemoteConfigParam.FORCE_UPDATE_VERSION.key)
            ?: UpdateManager.DEFAULT_FORCE_UPDATE_VERSION

}