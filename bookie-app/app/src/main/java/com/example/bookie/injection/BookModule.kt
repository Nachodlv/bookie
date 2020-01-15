package com.example.bookie.injection

import com.example.bookie.MyApplication
import com.example.bookie.api.client.BookApiClient
import com.example.bookie.api.client.BookClient
import com.example.bookie.dao.AppDatabase
import com.example.bookie.dao.BookDao
import com.example.bookie.repositories.BookRepository
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton

val bookModule = Kodein.Module {

    bind<BookDao>() with singleton {
        val database: AppDatabase = instance()
        database.bookDao()
    }

    bind<BookApiClient>() with singleton {
        BookApiClient(MyApplication.appContext)
    }

    bind<BookClient>() with singleton {
        BookClient(MyApplication.appContext)
    }

    bind<BookRepository>() with singleton {
        BookRepository(instance(), instance(), instance(), instance())
    }
}