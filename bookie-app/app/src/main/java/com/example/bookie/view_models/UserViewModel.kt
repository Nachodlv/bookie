package com.example.bookie.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.bookie.models.User
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.repositories.UserRepository

/**
* Get view model from view

        val injector = KodeinInjector()
        val model: UserViewModel by injector.instance()

       override fun onCreate(savedInstanceState: Bundle?) {
           injector.inject(appKodein())
       }

* */

class UserViewModel constructor(private val userRepository: UserRepository) :
    ViewModel() {

    var user: LiveData<RepositoryStatus<User>>? = null
        private set

    fun init(id: String) {
        if (user != null) return
        user = userRepository.getUser(id)
    }

}