package com.ferhatozcelik.core.extensions

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Size

class OnSingleClickListener(
    private val interval: Long = 1000, private val block: (View) -> Unit
) : View.OnClickListener {
    private var lastClickTime: Long = 0
    override fun onClick(view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < interval) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
        block(view)
    }
}

fun View.setOnClickListener(@Size(min = 1L, max = 40L) eventName: String, bundle: Bundle? = null, listener: View.OnClickListener) {
    setOnClickListener {
        listener.onClick(it)
    }
}

fun View.setOnSingleClickListener(
    @Size(min = 1L, max = 40L) eventName: String, bundle: Bundle? = null, listener: View.OnClickListener
) {
    setOnClickListener(OnSingleClickListener {
        listener.onClick(it)
    })
}

fun View.setOnSingleClickListener(listener: View.OnClickListener
) {
    setOnClickListener(OnSingleClickListener {
        listener.onClick(it)
    })
}

@SuppressLint("ClickableViewAccessibility")
fun View.setOnScaleClickListener(listener: View.OnClickListener) {
    setOnAnimationClickListener(listener,
        { animate().setDuration(50).scaleX(0.9f).scaleY(0.9f).start() },
        { animate().setDuration(100).scaleX(1f).scaleY(1f).start() })
}

@SuppressLint("ClickableViewAccessibility")
fun View.setOnRotationClickListener(listener: View.OnClickListener) {
    setOnAnimationClickListener(
        listener,
        { animate().setDuration(500).rotation(90f).start() },
        { animate().setDuration(1000).rotation(0f).start() },
    )
}

@SuppressLint("ClickableViewAccessibility")
private fun View.setOnAnimationClickListener(listener: View.OnClickListener, clickAnimation: () -> Unit, releaseAnimation: () -> Unit) {
    var released = false
    var lastClickTime: Long = 0
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (SystemClock.elapsedRealtime() - lastClickTime > 500) {
                    clickAnimation()
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                releaseAnimation()
            }

            MotionEvent.ACTION_UP -> {
                releaseAnimation()
                if (!released && SystemClock.elapsedRealtime() - lastClickTime > 500) {
                    Handler(Looper.getMainLooper()).postDelayed({ listener.onClick(this) }, 100)
                    lastClickTime = SystemClock.elapsedRealtime()
                }
                released = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (released) {
                    releaseAnimation()
                    return@setOnTouchListener true
                }
                val x = event.x;
                val y = event.y;
                released = x < 0 || y < 0 || x > width || y > height
                if (released) releaseAnimation()
            }
        }
        return@setOnTouchListener true
    }
}