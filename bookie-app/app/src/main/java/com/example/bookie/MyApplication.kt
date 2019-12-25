package com.example.bookie

import android.app.Application
import android.content.Context
import com.example.bookie.injection.ApplicationComponent
import dagger.android.support.DaggerApplication


class MyApplication : Application() {

//    val appComponent = Dagger.create()


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        private var context: Context? = null
        val appContext: Context?
            get() = context
    }
}