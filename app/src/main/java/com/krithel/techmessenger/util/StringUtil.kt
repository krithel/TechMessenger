package com.krithel.techmessenger.util

/**
 * Created by Krithel on 08-Mar-16.
 */
fun String.isEmail(): Boolean {
    return matches(Regex("""\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}\b"""))
}

fun String.isValidPassword(): Boolean {
    return length > 5
}