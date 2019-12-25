package com.example.bookie.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bookie.api.client.UserClient
import com.example.bookie.dao.UserDao
import com.example.bookie.models.User
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/*
* Get view model from view
*   val model = ViewModelProviders.of(this)[UsersViewModel::class.java]
        model.getUsers().observe(this, Observer<List<User>>{ users ->
            // update UI
        })
*
* */
@Singleton
class UserRepository @Inject constructor(
    private val userClient: UserClient,
    // Simple in-memory cache. Details omitted for brevity.
    private val executor: Executor,
    private val userDao: UserDao
) {
    private val status: MutableLiveData<RepositoryStatus<User>> by lazy {
        val list = MutableLiveData<RepositoryStatus<User>>()
        list.value = RepositoryStatus.Loading()
        list
    }

    fun getUser(userId: String): LiveData<RepositoryStatus<User>> {
        refreshUser(userId)
        // Returns a LiveData object directly from the database.
        return status
    }

    private fun refreshUser(userId: String) {
        // Runs in a background thread.
        executor.execute {
            // Check if user data was fetched recently.
            val userExists = userDao.hasUser(userId, FRESH_TIMEOUT)
            if (userExists == null) {
                // Refreshes the data.
                userClient.getUserById(userId) { user, error ->
                    run {
                        if (user != null) {
                            user.let { userDao.save(it) }
                            status.value = RepositoryStatus.Success(user)
                        } else {
                            status.value = RepositoryStatus.Error(error)
                        }

                    }
                }
            } else status.value = RepositoryStatus.Success(userExists)
        }
    }

    companion object {
        val FRESH_TIMEOUT = TimeUnit.DAYS.toMillis(1)
    }
}
