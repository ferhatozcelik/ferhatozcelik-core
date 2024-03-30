package com.ferhatozcelik.firebase.model

/**
 * Created by ferhatozcelik on 27.03.2024.
 */

import androidx.annotation.DrawableRes
import androidx.annotation.Keep

/**
 * Data class for push notification
 * @param smallIcon: Int?
 * @param channelList: List<Channel>
 *
 */

@Keep
data class PushNotification(
    @DrawableRes val smallIcon: Int? = null,
    val channelList: List<Channel> = emptyList(),
)

/**
 * Data class for channel
 * @param id: String
 * @param name: String
 *
 */

@Keep
data class Channel(
    val id: String,
    val name: String
)