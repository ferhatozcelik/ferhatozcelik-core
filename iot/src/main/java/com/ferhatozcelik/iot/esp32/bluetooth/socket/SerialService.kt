package com.ferhatozcelik.iot.esp32.bluetooth.socket

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ferhatozcelik.iot.esp32.bluetooth.listener.SerialListener
import com.ferhatozcelik.iot.esp32.bluetooth.util.Constants
import com.ferhatozcelik.iot.esp32.bluetooth.util.Constants.getIntentActionDisconnect
import com.ferhatozcelik.iot.esp32.bluetooth.util.Constants.getIntentClassMainActivity
import com.ferhatozcelik.iot.esp32.bluetooth.util.Constants.getNotificationChannel
import java.io.IOException
import java.util.ArrayDeque

/**
 * create notification and queue serial data while activity is not in the foreground
 * use listener chain: SerialSocket -> SerialService -> UI fragment
 */
class SerialService : Service(), SerialListener {
    inner class SerialBinder : Binder() {
        val service: SerialService
            get() = this@SerialService
    }

    private enum class QueueType {
        Connect,
        ConnectError,
        Read,
        IoError
    }

    private class QueueItem {
        var type: QueueType
        var datas: ArrayDeque<ByteArray>? = null
        var e: Exception? = null

        constructor(type: QueueType) {
            this.type = type
            if (type == QueueType.Read) init()
        }

        constructor(type: QueueType, e: Exception?) {
            this.type = type
            this.e = e
        }

        constructor(type: QueueType, datas: ArrayDeque<ByteArray>?) {
            this.type = type
            this.datas = datas
        }

        fun init() {
            datas = ArrayDeque()
        }

        fun add(data: ByteArray) {
            datas!!.add(data)
        }
    }

    private val mainLooper: Handler
    private val binder: IBinder
    private val queue1: ArrayDeque<QueueItem>
    private val queue2: ArrayDeque<QueueItem>
    private val lastRead: QueueItem
    private var socket: SerialSocket? = null
    private var listener: SerialListener? = null
    private var connected = false

    /**
     * Lifecylce
     */
    init {
        mainLooper = Handler(Looper.getMainLooper())
        binder = SerialBinder()
        queue1 = ArrayDeque()
        queue2 = ArrayDeque()
        lastRead = QueueItem(QueueType.Read)
    }

    override fun onDestroy() {
        cancelNotification()
        disconnect()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    /**
     * Api
     */
    @Throws(IOException::class)
    fun connect(socket: SerialSocket) {
        socket.connect(this)
        this.socket = socket
        connected = true
    }

    fun disconnect() {
        connected = false // ignore data,errors while disconnecting
        cancelNotification()
        if (socket != null) {
            socket!!.disconnect()
            socket = null
        }
    }

    @Throws(IOException::class)
    fun write(data: ByteArray?) {
        if (!connected) throw IOException("not connected")
        socket!!.write(data)
    }

    fun attach(listener: SerialListener) {
        require(Looper.getMainLooper().thread === Thread.currentThread()) { "not in main thread" }
        initNotification()
        cancelNotification()
        // use synchronized() to prevent new items in queue2
        // new items will not be added to queue1 because mainLooper.post and attach() run in main thread
        synchronized(this) { this.listener = listener }
        for (item in queue1) {
            when (item.type) {
                QueueType.Connect -> listener.onSerialConnect()
                QueueType.ConnectError -> listener.onSerialConnectError(item.e)
                QueueType.Read -> listener.onSerialRead(item.datas)
                QueueType.IoError -> listener.onSerialIoError(item.e)
            }
        }
        for (item in queue2) {
            when (item.type) {
                QueueType.Connect -> listener.onSerialConnect()
                QueueType.ConnectError -> listener.onSerialConnectError(item.e)
                QueueType.Read -> listener.onSerialRead(item.datas)
                QueueType.IoError -> listener.onSerialIoError(item.e)
            }
        }
        queue1.clear()
        queue2.clear()
    }

    fun detach() {
        if (connected) createNotification()
        // items already in event queue (posted before detach() to mainLooper) will end up in queue1
        // items occurring later, will be moved directly to queue2
        // detach() and mainLooper.post run in the main thread, so all items are caught
        listener = null
    }

    private fun initNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nc = NotificationChannel(
                getNotificationChannel(this),
                "Background service",
                NotificationManager.IMPORTANCE_LOW
            )
            nc.setShowBadge(false)
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(nc)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun areNotificationsEnabled(): Boolean {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val nc = nm.getNotificationChannel(getNotificationChannel(this))
        return nm.areNotificationsEnabled() && nc != null && nc.importance > NotificationManager.IMPORTANCE_NONE
    }

    private fun createNotification() {
        val disconnectIntent = Intent()
            .setPackage(packageName)
            .setAction(getIntentActionDisconnect(this))
        val restartIntent = Intent()
            .setClassName(this, getIntentClassMainActivity(this))
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val disconnectPendingIntent = PendingIntent.getBroadcast(this, 1, disconnectIntent, flags)
        val restartPendingIntent = PendingIntent.getActivity(this, 1, restartIntent, flags)
        val builder = NotificationCompat.Builder(this, getNotificationChannel(this))
            .setSmallIcon(applicationInfo.icon)
            .setColor(ContextCompat.getColor(this, android.R.color.white))
            .setContentTitle(applicationInfo.name)
            .setContentText(if (socket != null) "Connected to " + socket?.name else "Background Service")
            .setContentIntent(restartPendingIntent)
            .setOngoing(true)
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_notification_clear_all,
                    "Disconnect",
                    disconnectPendingIntent
                )
            )
        // @drawable/ic_notification created with Android Studio -> New -> Image Asset using @color/colorPrimaryDark as background color
        // Android < API 21 does not support vectorDrawables in notifications, so both drawables used here, are created as .png instead of .xml
        val notification = builder.build()
        startForeground(Constants.NOTIFY_MANAGER_START_FOREGROUND_SERVICE, notification)
    }

    private fun cancelNotification() {
        stopForeground(true)
    }

    /**
     * SerialListener
     */
    override fun onSerialConnect() {
        if (connected) {
            synchronized(this) {
                if (listener != null) {
                    mainLooper.post {
                        if (listener != null) {
                            listener?.onSerialConnect()
                        } else {
                            queue1.add(QueueItem(QueueType.Connect))
                        }
                    }
                } else {
                    queue2.add(QueueItem(QueueType.Connect))
                }
            }
        }
    }

    override fun onSerialConnectError(e: Exception?) {
        if (connected) {
            synchronized(this) {
                if (listener != null) {
                    mainLooper.post {
                        if (listener != null) {
                            listener!!.onSerialConnectError(e)
                        } else {
                            queue1.add(QueueItem(QueueType.ConnectError, e))
                            disconnect()
                        }
                    }
                } else {
                    queue2.add(QueueItem(QueueType.ConnectError, e))
                    disconnect()
                }
            }
        }
    }

    override fun onSerialRead(datas: ArrayDeque<ByteArray>?) {
        throw UnsupportedOperationException()
    }

    /**
     * reduce number of UI updates by merging data chunks.
     * Data can arrive at hundred chunks per second, but the UI can only
     * perform a dozen updates if receiveText already contains much text.
     *
     * On new data inform UI thread once (1).
     * While not consumed (2), add more data (3).
     */
    override fun onSerialRead(data: ByteArray) {
        if (connected) {
            synchronized(this) {
                if (listener != null) {
                    var first: Boolean
                    synchronized(lastRead) {
                        first = lastRead.datas!!.isEmpty() // (1)
                        lastRead.add(data) // (3)
                    }
                    if (first) {
                        mainLooper.post {
                            var datas: ArrayDeque<ByteArray>?
                            synchronized(lastRead) {
                                datas = lastRead.datas
                                lastRead.init() // (2)
                            }
                            if (listener != null) {
                                listener!!.onSerialRead(datas)
                            } else {
                                queue1.add(QueueItem(QueueType.Read, datas))
                            }
                        }
                    }
                } else {
                    if (queue2.isEmpty() || queue2.last.type != QueueType.Read) queue2.add(
                        QueueItem(
                            QueueType.Read
                        )
                    )
                    queue2.last.add(data)
                }
            }
        }
    }

    override fun onSerialIoError(e: Exception?) {
        if (connected) {
            synchronized(this) {
                if (listener != null) {
                    mainLooper.post {
                        if (listener != null) {
                            listener!!.onSerialIoError(e)
                        } else {
                            queue1.add(QueueItem(QueueType.IoError, e))
                            disconnect()
                        }
                    }
                } else {
                    queue2.add(QueueItem(QueueType.IoError, e))
                    disconnect()
                }
            }
        }
    }

}
