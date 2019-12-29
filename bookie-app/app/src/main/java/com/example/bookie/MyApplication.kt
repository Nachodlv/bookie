package com.example.bookie

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.example.bookie.api.client.UserClient
import com.example.bookie.dao.UserDao
import com.example.bookie.dao.UserDatabase
import com.example.bookie.injection.modules
import com.example.bookie.repositories.UserRepository
import com.example.bookie.view_models.UserViewModel
import com.github.salomonbrys.kodein.*
import java.util.concurrent.Executors

class MyApplication : Application(), KodeinAware {


    override val kodein: Kodein = Kodein {
        bind<UserDatabase>() with singleton {
            Room.databaseBuilder(this@MyApplication, UserDatabase::class.java, "bookie-db")
                .build() }
        import(modules)
    }

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

