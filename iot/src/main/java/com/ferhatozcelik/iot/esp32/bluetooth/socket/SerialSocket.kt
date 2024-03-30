package com.ferhatozcelik.iot.esp32.bluetooth.socket

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.ferhatozcelik.iot.esp32.bluetooth.listener.SerialListener
import com.ferhatozcelik.iot.esp32.bluetooth.util.Constants
import java.io.IOException
import java.security.InvalidParameterException
import java.util.UUID
import java.util.concurrent.Executors

class SerialSocket(
    private val context: Context, private val device: BluetoothDevice, private val uuid: UUID
) : Runnable {
    private val disconnectBroadcastReceiver: BroadcastReceiver
    private var listener: SerialListener? = null
    private var socket: BluetoothSocket? = null
    private var connected = false

    init {
        if (context is Activity) throw InvalidParameterException("expected non UI context")
        disconnectBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (listener != null) {
                    listener!!.onSerialIoError(IOException("background disconnect"))
                    disconnect() // disconnect now, else would be queued until UI re-attached
                }
            }
        }
    }

    val name: String
        @SuppressLint("MissingPermission")
        get() = if (device.name != null) device.name else device.address

    /**
     * connect-success and most connect-errors are returned asynchronously to listener
     */
    @Throws(IOException::class)
    fun connect(listener: SerialListener?) {
        this.listener = listener
        ContextCompat.registerReceiver(
            context,
            disconnectBroadcastReceiver,
            IntentFilter(Constants.getIntentActionDisconnect(context)),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        Executors.newSingleThreadExecutor().submit(this)
    }

    fun disconnect() {
        listener = null // ignore remaining data and errors
        // connected = false; // run loop will reset connected
        if (socket != null) {
            try {
                socket!!.close()
            } catch (ignored: Exception) {
            }
            socket = null
        }
        try {
            context.unregisterReceiver(disconnectBroadcastReceiver)
        } catch (ignored: Exception) {
        }
    }

    @Throws(IOException::class)
    fun write(data: ByteArray?) {
        if (!connected) throw IOException("not connected")
        socket!!.outputStream.write(data)
    }

    @SuppressLint("MissingPermission")
    override fun run() { // connect & read
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket?.connect()
            if (listener != null) listener!!.onSerialConnect()
        } catch (e: Exception) {
            if (listener != null) listener!!.onSerialConnectError(e)
            try {
                socket!!.close()
            } catch (ignored: Exception) {
            }
            socket = null
            return
        }
        connected = true
        try {
            val buffer = ByteArray(1024)
            var len: Int
            while (true) {
                len = socket?.inputStream?.read(buffer)!!
                val data = buffer.copyOf(len)
                if (listener != null) listener!!.onSerialRead(data)
            }
        } catch (e: Exception) {
            connected = false
            if (listener != null) listener!!.onSerialIoError(e)
            try {
                socket?.close()
            } catch (ignored: Exception) {
            }
            socket = null
        }
    }

}
