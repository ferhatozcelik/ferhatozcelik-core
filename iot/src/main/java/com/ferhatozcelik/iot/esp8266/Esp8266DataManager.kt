package com.ferhatozcelik.iot.esp8266

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import com.ferhatozcelik.iot.esp8266.listener.WifiDataListener
import com.ferhatozcelik.iot.esp8266.services.EspApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Esp8266DataManager(private val context: Context) {

    private var timer: CountDownTimer? = null
    private val TAG = "Esp8266DataManager"

    private var esp8266WifiManager: Esp8266WifiManager = Esp8266WifiManager.getInstance(context)

    private var address = "http://192.168.100.100/"

    private var wifiDataListener: WifiDataListener? = null

    companion object {
        private const val TAG = "Esp8266WifiManager"

        @SuppressLint("StaticFieldLeak")
        private var instance: Esp8266DataManager? = null

        fun getInstance(context: Context): Esp8266DataManager {
            if (instance == null) {
                instance = Esp8266DataManager(context)
            }
            return instance as Esp8266DataManager
        }

    }

    fun setAddress(address: String) {
        this.address = address
    }

    fun setWifiDataListener(wifiDataListener: WifiDataListener) {
        this.wifiDataListener = wifiDataListener
    }

    fun startReadingData() {

        timer = object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                if (esp8266WifiManager.isWifiConnected()) {
                    requestEsp8266Data()
                    startReadingData()
                } else {
                    wifiDataListener?.onWifiDataError("Wifi is not connected")
                    stopReadingData()
                }
            }
        }
        timer?.start()

    }

    fun stopReadingData() {
        timer?.cancel()
    }

    private fun requestEsp8266Data() {

        val espService = EspApi(address).service
        espService.getData().enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                Log.d(TAG, "onResponse: ${response.body().toString()}")
                if (response.isSuccessful && response.body() != null) {
                    wifiDataListener?.onWifiDataReceived(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
                t.message?.let {
                    wifiDataListener?.onWifiDataError(it)
                }
            }
        })

    }

}