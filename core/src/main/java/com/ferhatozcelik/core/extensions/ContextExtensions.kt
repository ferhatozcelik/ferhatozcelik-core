package com.ferhatozcelik.core.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity


@SuppressLint("RestrictedApi")
fun Context.getActivity(): Activity = when (this) {
    is ComponentActivity -> this
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> this.applicationContext.getActivity()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.goURL(url: String) {
    try {
        val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(myIntent)
    } catch (e: ActivityNotFoundException) {
        this.toast("No application can handle this request. Please install a webbrowser")
        e.printStackTrace()
    }
}

fun Context?.isContextInvalid(): Boolean {
    return this == null
}

