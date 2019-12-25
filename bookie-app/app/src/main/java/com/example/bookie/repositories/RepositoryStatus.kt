package com.example.bookie.repositories

sealed class RepositoryStatus<T> {
    class Loading<T> : RepositoryStatus<T>()
    data class Error<T>(val error: String) : RepositoryStatus<T>()
    data class Success<T>(val data: T): RepositoryStatus<T>()
}