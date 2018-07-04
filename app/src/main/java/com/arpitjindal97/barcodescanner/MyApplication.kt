package com.arpitjindal97.barcodescanner

import android.app.Application
import com.arpitjindal97.barcodescanner.di.AppComponent
import com.arpitjindal97.barcodescanner.di.AppModule
import com.arpitjindal97.barcodescanner.di.DaggerAppComponent

class MyApplication : Application() {

    companion object {

        lateinit var appComponent: AppComponent

    }
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        appComponent.inject(this)

    }
}