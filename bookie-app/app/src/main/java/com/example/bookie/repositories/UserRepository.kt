package com.example.bookie.repositories

import androidx.lifecycle.LiveData
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
    fun getUser(userId: String): LiveData<User> {
        refreshUser(userId)
        // Returns a LiveData object directly from the database.
        return userDao.getById(userId)
    }

    private fun refreshUser(userId: String) {
        // Runs in a background thread.
        executor.execute {
            // Check if user data was fetched recently.
            val userExists = userDao.hasUser(userId, FRESH_TIMEOUT)
            if (!userExists) {
                // Refreshes the data.
                userClient.getUserById(userId) { user, _ -> user?.let { userDao.save(it) } }

            }
        }
    }

    companion object {
        val FRESH_TIMEOUT = TimeUnit.DAYS.toMillis(1)
    }
}
