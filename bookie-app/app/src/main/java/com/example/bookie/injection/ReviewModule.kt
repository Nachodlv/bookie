package com.example.bookie.injection

import com.example.bookie.MyApplication
import com.example.bookie.api.client.ReviewClient
import com.example.bookie.dao.AppDatabase
import com.example.bookie.dao.ReviewDao
import com.example.bookie.repositories.ReviewRepository
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton

val reviewModule = Kodein.Module {

    bind<ReviewDao>() with singleton {
        val database: AppDatabase = instance()
        database.reviewDao()
    }

    bind<ReviewClient>() with singleton {
        ReviewClient(MyApplication.appContext)
    }

    bind<ReviewRepository>() with singleton {
        ReviewRepository(instance(), instance(), instance(), instance(), instance())
    }
}