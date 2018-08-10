package com.nrohmen.roomchat.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun toDateFormat(date: String): Date? {
    val formatter = SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy")
    return formatter.parse(date)
}

@SuppressLint("SimpleDateFormat")
fun getDate(date: Date?): String? = with(date ?: Date()) {
    SimpleDateFormat("EEE, dd MMM yyy").format(this)
}

@SuppressLint("SimpleDateFormat")
fun getTime(date: Date?): String? = with(date ?: Date()) {
    SimpleDateFormat("HH:mm").format(this)
}
