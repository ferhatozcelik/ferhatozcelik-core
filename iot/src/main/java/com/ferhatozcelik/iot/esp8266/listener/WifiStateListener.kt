package com.ferhatozcelik.iot.esp8266.listener

interface WifiStateListener {
    fun onWifiConnecting()
    fun onWifiConnected()
    fun onWifiDisconnected()
}
