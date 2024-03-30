package com.ferhatozcelik.spincoater.common.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat


inline fun <reified T : Activity> Activity.gotoActivity(
    bundle: Bundle? = null,
    data: Uri? = null,
    isFinish: Boolean = false
) {
    val i = Intent(this, T::class.java)
    if (bundle != null && !bundle.isEmpty) {
        i.putExtras(bundle)
    }

    if (data != null) {
        i.data = data
    }

    startActivity(i)

    if (isFinish) {
        this.finish()
    }
}

@SuppressLint("ObsoleteSdkInt")
fun Activity.setCustomStatusBarColor(statusBarColor: Int, isIconDark: Boolean = false) {
    window.statusBarColor = ContextCompat.getColor(this, statusBarColor)

    if (isIconDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView: View = window.decorView
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.setSystemBarsAppearance(
                0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView: View = window.decorView
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility = 0
        }
    }
}

@SuppressLint("ObsoleteSdkInt")
fun Activity.setCustomNavigationBarColor(navigationBarColor: Int, isIconDark: Boolean = false) {
    window.navigationBarColor = ContextCompat.getColor(this, navigationBarColor)
    if (isIconDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decorView: View = window.decorView
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.setSystemBarsAppearance(
                0, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView: View = window.decorView
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility = 0
        }
    }

}

fun Activity?.isContextInvalid(): Boolean {
    var destroyed = false
    if (this != null) {
        destroyed = this.isDestroyed
    }
    return this == null || destroyed || this.isFinishing
}