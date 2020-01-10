package com.example.bookie.repositories

import androidx.lifecycle.MutableLiveData

sealed class RepositoryStatus<T> {
    class Loading<T> : RepositoryStatus<T>()
    data class Error<T>(val error: String) : RepositoryStatus<T>()
    data class Success<T>(val data: T): RepositoryStatus<T>()

    companion object {
        fun <T> initStatus(): MutableLiveData<RepositoryStatus<T>> {
            return MutableLiveData(Loading())
        }
    }
}