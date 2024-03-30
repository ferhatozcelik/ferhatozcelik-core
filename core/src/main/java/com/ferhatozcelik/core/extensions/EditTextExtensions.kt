package com.ferhatozcelik.spincoater.common.extensions

import android.widget.EditText
import com.ferhatozcelik.spincoater.R

fun EditText.modifyText(numberText: String) {
    this.setText(numberText)
    this.setSelection(numberText.length)
}

fun EditText.valuePlus(minValue: Int, maxValue: Int, increment: Int) {
    if (!this.text.isNullOrEmpty()) {
        var value = this.text.toString().toInt()
        if (value in minValue until maxValue) {
            value += increment
        }
        this.setText(value.toString())
        this.setSelection(this.text.length)
    } else {
        context.toast(context.getString(R.string.please_enter_value))
    }

}

fun EditText.valueMinus(minValue: Int, maxValue: Int, increment: Int) {
    if (!this.text.isNullOrEmpty()) {
        var value = this.text.toString().toInt()
        if (value in (minValue + 1)..maxValue) {
            value -= increment
        }
        this.setText(value.toString())
        this.setSelection(this.text.length)
    } else {
        context.toast(context.getString(R.string.please_enter_value))
    }
}