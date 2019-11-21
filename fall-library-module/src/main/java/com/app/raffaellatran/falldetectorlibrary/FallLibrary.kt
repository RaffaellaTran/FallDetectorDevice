package com.app.raffaellatran.falldetectorlibrary

import android.app.Application

class FallLibrary : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .fallModule(FallModule(applicationContext))
            .build()
        appComponent
            .inject(this)
    }
}