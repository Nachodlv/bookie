package com.example.bookie.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.bookie.models.User
import com.example.bookie.repositories.RepositoryStatus
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

class UserViewModel :
    ViewModel() {

    var repository: UserRepository? = null
        @Inject set

    var user: LiveData<RepositoryStatus<User>>? = null
    get() = field
    private set

    fun init(id: String) {
        if (user != null || repository == null) return
        user = repository!!.getUser(id)
    }

}