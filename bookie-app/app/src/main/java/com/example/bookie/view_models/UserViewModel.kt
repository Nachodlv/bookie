package com.example.bookie.view_models

import androidx.lifecycle.*
import com.example.bookie.models.User
import com.example.bookie.repositories.UserRepository
import javax.inject.Inject

/*
* Get view model from view
*   val model = ViewModelProviders.of(this)[UsersViewModel::class.java]
        model.getUsers().observe(this, Observer<List<User>>{ users ->
            // update UI
        })
*
* */

class UserViewModel(@Inject private val repository: UserRepository):
    ViewModel() {

    private var user: LiveData<User>? = null

    fun init(id: String) {
        if(user != null) return
        user = repository.getUser(id)
    }

}