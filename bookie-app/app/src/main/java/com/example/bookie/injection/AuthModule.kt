package com.example.bookie.injection

import com.example.bookie.MyApplication
import com.example.bookie.api.client.AuthClient
import com.example.bookie.dao.SharedPreferencesDao
import com.example.bookie.repositories.AuthRepository
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton

val authModule = Kodein.Module {
    bind<SharedPreferencesDao>() with singleton {
        SharedPreferencesDao(MyApplication.appContext)
    }

    bind<AuthClient>() with singleton {
        AuthClient(MyApplication.appContext, instance())
    }

    bind<AuthRepository>() with singleton {
        AuthRepository(instance(), instance(), instance(), instance())
    }
}