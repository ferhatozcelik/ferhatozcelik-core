package com.ferhatozcelik.iot.esp32.bluetooth.listener

import android.bluetooth.BluetoothDevice

interface DeviceCallback {
    fun onDeviceFound(deviceList: ArrayList<BluetoothDevice>)

    fun onDeviceNotFound(error: String)

}