package com.example.bookie.ui.profile.header

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookie.models.User

class ProfileHeaderViewModel : ViewModel() {

    private val _user = MutableLiveData<User>()
    private val _followers = MutableLiveData<Int>()

    val user: LiveData<User> = _user
    val followers: LiveData<Int> = _followers

    fun storeUser(user: User) {
        _user.value = user
    }

    fun storeFollowers(amount: Int) {
        _followers.value = amount
    }
}