package com.example.bookie.injection

import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
class ExecutorModule {
    @Singleton
    @Provides
    fun providesExecutor(): Executor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
}