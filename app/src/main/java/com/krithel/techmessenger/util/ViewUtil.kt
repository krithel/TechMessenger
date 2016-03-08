package com.krithel.techmessenger.util

import android.support.design.widget.Snackbar
import android.view.View
import android.widget.EditText

/**
 * Created by Krithel on 08-Mar-16.
 */
var EditText.enteredText: String
    get() = text.toString()
    set(s: String) = setText(s)

fun View.makeSnack(msg: String, length: Int): Unit {
    Snackbar.make(this, msg, length).show()
}