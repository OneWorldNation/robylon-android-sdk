package com.own.android.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("StaticFieldLeak")
object AppCreator {

    lateinit var context: Context

    val pref: SharedPreferences by lazy {
        context.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)
    }

}