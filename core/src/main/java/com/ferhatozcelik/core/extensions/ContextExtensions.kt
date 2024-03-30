package com.ferhatozcelik.core.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity

@SuppressLint("RestrictedApi")
fun Context.getActivity(): Activity = when (this) {
    is ComponentActivity -> this
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> this.applicationContext.getActivity()
}