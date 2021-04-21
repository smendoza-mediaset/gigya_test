package com.mediaset.gigyatest

import android.app.Application
import com.gigya.android.sdk.Gigya

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        Gigya.setApplication(this)
    }
}