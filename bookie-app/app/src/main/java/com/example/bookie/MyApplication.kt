package com.example.bookie

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.bookie.dao.AppDatabase
import com.example.bookie.injection.modules
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton

class MyApplication : Application(), KodeinAware {


    override val kodein: Kodein = Kodein {
        bind<AppDatabase>() with singleton {
            Room.databaseBuilder(this@MyApplication, AppDatabase::class.java, "bookie-db")
                .fallbackToDestructiveMigration()
                .build()
        }
        import(modules)
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        MyApplication.kodein = kodein
    }

    companion object {
        private var context: Context? = null
        private lateinit var kodein: Kodein

        val appContext: Context?
            get() = context

        val appKodein: Kodein
            get() = kodein
    }
}

