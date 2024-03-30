package com.ferhatozcelik.core.extensions

import androidx.fragment.app.Fragment

fun Fragment?.isContextInvalid(): Boolean {
    return this == null || this.isRemoving
}

fun Fragment?.isContextValid(): Boolean {
    return !(this == null || this.isRemoving)
}

