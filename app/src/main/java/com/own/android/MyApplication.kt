package com.own.android

import android.app.Application
import app.own.Robylon
import com.own.android.base.AppCreator

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCreator.context = this
        try {
            Robylon.initialize(
                context = this,
                apiKey = BuildConfig.OWN_API_KEY,
            )
        } catch (e: Exception) {
            //Do nothing
        }
    }
}