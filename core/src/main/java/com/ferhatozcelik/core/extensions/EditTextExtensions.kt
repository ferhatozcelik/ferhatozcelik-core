package com.ferhatozcelik.core.extensions

import android.widget.EditText

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
    }
}