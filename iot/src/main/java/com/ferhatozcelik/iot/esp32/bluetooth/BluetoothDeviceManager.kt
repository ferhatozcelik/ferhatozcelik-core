package com.ferhatozcelik.iot.esp32.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.ferhatozcelik.iot.esp32.bluetooth.listener.DeviceCallback
import com.ferhatozcelik.iot.esp32.bluetooth.util.BluetoothUtil
import java.util.Collections

class BluetoothDeviceManager(private var activity: Activity) {

    private var permissionMissing = false
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val listItems = ArrayList<BluetoothDevice>()
    private var deviceCallback: DeviceCallback? = null

    init {
        if (activity.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            val bluetoothManager =
                activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter
        }
    }

    fun getBluetoothAdapter(): BluetoothAdapter {
        return bluetoothAdapter
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter.isEnabled
    }

    fun setListener(deviceCallback: DeviceCallback) {
        this.deviceCallback = deviceCallback
    }

    @SuppressLint("MissingPermission")
    fun refresh() {
        listItems.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionMissing =
                activity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        }
        if (!permissionMissing) {
            for (device in bluetoothAdapter.bondedDevices) {
                if (device.type != BluetoothDevice.DEVICE_TYPE_LE) {
                    listItems.add(device)
                }
            }
            Collections.sort(listItems, BluetoothUtil::compareTo)
        }
        deviceCallback?.onDeviceFound(listItems)
        if (!bluetoothAdapter.isEnabled) {
            deviceCallback?.onDeviceNotFound("bluetooth is disabled")
        } else if (permissionMissing) {
            deviceCallback?.onDeviceNotFound("permission missing, use REFRESH")
        }
    }

}