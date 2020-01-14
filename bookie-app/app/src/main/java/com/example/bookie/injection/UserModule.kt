package com.example.bookie.injection

import com.example.bookie.MyApplication
import com.example.bookie.api.client.UserClient
import com.example.bookie.dao.AppDatabase
import com.example.bookie.dao.UserDao
import com.example.bookie.repositories.UserRepository
import com.example.bookie.view_models.UserViewModel
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import java.util.concurrent.Executors

val userModule = Kodein.Module {

    bind<UserDao>() with singleton {
        val database: AppDatabase = instance()
        database.userDao()
    }

    bind<UserClient>() with singleton {
        UserClient(MyApplication.appContext)
    }

    bind<UserRepository>() with singleton {
        UserRepository(
            instance(),
            instance(),
            instance(),
            instance(),
            instance()
        )
    }

    bind<UserViewModel>() with singleton {
        UserViewModel(instance())
    }
}