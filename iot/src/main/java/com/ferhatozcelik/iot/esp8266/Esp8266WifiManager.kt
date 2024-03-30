package com.ferhatozcelik.iot.esp8266

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.CountDownTimer
import com.ferhatozcelik.iot.esp8266.listener.WifiStateListener

private const val TAG = "Esp8266WifiManager"

class Esp8266WifiManager(private val context: Context) {

    private var wifiStateListener: WifiStateListener? = null
    private val timeout = 10000L
    private var isWifiConnected = false

    companion object {
        private const val TAG = "Esp8266WifiManager"

        @SuppressLint("StaticFieldLeak")
        private var instance: Esp8266WifiManager? = null

        fun getInstance(context: Context): Esp8266WifiManager {
            if (instance == null) {
                instance = Esp8266WifiManager(context)
            }
            return instance as Esp8266WifiManager
        }

    }

    fun setWifiStateListener(wifiStateListener: WifiStateListener) {
        this.wifiStateListener = wifiStateListener
    }

    fun isWifiConnected(): Boolean {
        return isWifiConnected
    }

    fun disconnect() {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.disconnect()
    }

    fun connectToWifi(ssid: String, password: String) {

        wifiStateListener?.onWifiConnecting()

        object : CountDownTimer(timeout, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                if (!isWifiConnected) {
                    wifiStateListener?.onWifiDisconnected()
                    isWifiConnected = false
                }

            }
        }.start()

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val wifiNetworkSpecifier =
                WifiNetworkSpecifier.Builder().setSsid(ssid).setWpa2Passphrase(password).build()

            val networkRequest =
                NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .setNetworkSpecifier(wifiNetworkSpecifier).build()


            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onUnavailable() {
                    super.onUnavailable()
                    wifiStateListener?.onWifiDisconnected()
                    isWifiConnected = false
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    connectivityManager.bindProcessToNetwork(network)
                    wifiStateListener?.onWifiConnected()
                    isWifiConnected = true
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    wifiStateListener?.onWifiDisconnected()
                    isWifiConnected = false
                }
            }

            connectivityManager.requestNetwork(networkRequest, networkCallback)

        } else {
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            val wifiConfig = WifiConfiguration()
            wifiConfig.SSID = String.format("\"%s\"", ssid)
            wifiConfig.preSharedKey = String.format("\"%s\"", password)

            val netId = wifiManager.addNetwork(wifiConfig)
            wifiManager.disconnect()
            wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()


            if (wifiManager.isWifiEnabled && wifiManager.connectionInfo.ssid == "\"$ssid\"") {
                wifiStateListener?.onWifiConnected()
                isWifiConnected = true
            } else {
                wifiStateListener?.onWifiDisconnected()
                isWifiConnected = false
            }
        }

    }

}