package com.example.bookie.injection

import androidx.room.Room
import com.example.bookie.MyApplication
import com.example.bookie.api.client.UserClient
import com.example.bookie.dao.UserDao
import com.example.bookie.dao.UserDatabase
import com.example.bookie.repositories.UserRepository
import com.example.bookie.view_models.UserViewModel
import com.github.salomonbrys.kodein.*
import java.util.concurrent.Executors

val modules = Kodein.Module {

    bind<UserDao>() with singleton {
        val database: UserDatabase  = instance()
        database.userDao()
    }

    bind<UserClient>() with singleton {
        UserClient(MyApplication.appContext)
    }

    bind<UserRepository>() with singleton {
        UserRepository(
            instance(),
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()),
            instance()
        )
    }

    bind<UserViewModel>() with singleton {
        UserViewModel(instance())
    }
}