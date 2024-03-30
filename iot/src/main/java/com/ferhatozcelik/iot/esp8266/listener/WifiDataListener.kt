package com.ferhatozcelik.iot.esp8266.listener

interface WifiDataListener {
    fun onWifiDataReceived(data: Any)

    fun onWifiDataError(error: String)
}
