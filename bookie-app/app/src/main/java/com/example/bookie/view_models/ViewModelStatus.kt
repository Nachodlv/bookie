package com.example.bookie.view_models

sealed class ViewModelStatus {
    object Loading : ViewModelStatus()
    data class Error(val error: String) : ViewModelStatus()
    data class Success<T>(val data: T): ViewModelStatus()

}