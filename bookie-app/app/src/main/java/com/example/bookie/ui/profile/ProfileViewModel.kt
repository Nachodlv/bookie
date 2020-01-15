package com.example.bookie.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookie.models.User
import com.example.bookie.models.UserPreview

class ProfileViewModel : ViewModel() {

    private val _users = MutableLiveData<MutableList<UserPreview>>()

    val users: LiveData<MutableList<UserPreview>> = _users

    fun storeUser(users: MutableList<UserPreview>) {
        _users.postValue(users)
    }
}