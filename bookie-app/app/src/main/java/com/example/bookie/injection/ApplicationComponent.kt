package com.example.bookie.injection

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import java.util.concurrent.Executor
import java.util.concurrent.Executors

val modules = Kodein.Module {
    bind<Executor>() with singleton {
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    }

    import(authModule)
    import(userModule)
    import(bookModule)
    import(reviewModule)

}