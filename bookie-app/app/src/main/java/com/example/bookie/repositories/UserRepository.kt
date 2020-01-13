package com.example.bookie.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.bookie.api.client.UserClient
import com.example.bookie.dao.SharedPreferencesDao
import com.example.bookie.dao.UserDao
import com.example.bookie.models.Review
import com.example.bookie.models.User
import com.example.bookie.models.UserReviewResponse
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

/*
* Get view model from view
*   val model = ViewModelProviders.of(this)[UsersViewModel::class.java]
        model.getUsers().observe(this, Observer<List<User>>{ users ->
            // update UI
        })
*
* */
class UserRepository constructor(
    private val userClient: UserClient,
    // Simple in-memory cache. Details omitted for brevity.
    private val executor: Executor,
    private val userDao: UserDao,
    private val sharedPreferencesDao: SharedPreferencesDao
) {


    fun getUser(userId: String): LiveData<RepositoryStatus<User>> {
        val status = RepositoryStatus.initStatus<User>()
        refreshUser(userId, status)
        // Returns a LiveData object directly from the database.
        return status
    }

    fun getUserReviews(
        userId: String,
        index: Int,
        size: Int
    ): LiveData<RepositoryStatus<List<Review>>> {
        val status = RepositoryStatus.initStatus<List<UserReviewResponse>>()
        userClient.getUserReviews(userId, index / size, size, {
            executor.execute {
                status.postValue(RepositoryStatus.Success(it))
            }
        }, { executor.execute { status.postValue(RepositoryStatus.Error(it)) } })
        return Transformations.switchMap(getUser(userId)) { userStatus ->
            Transformations.map(status) { reviewStatus ->
                if (reviewStatus is RepositoryStatus.Success && userStatus is RepositoryStatus.Success) {
                    RepositoryStatus.Success(reviewStatus.data.map { it.toReview(userStatus.data) }) as RepositoryStatus<List<Review>>
                } else if (reviewStatus is RepositoryStatus.Error) {
                    RepositoryStatus.Error(reviewStatus.error)
                } else if (userStatus is RepositoryStatus.Error) {
                    RepositoryStatus.Error(userStatus.error)
                } else RepositoryStatus.Loading()
            }
        }

    }

    fun registerUser(
        email: String,
        password: String,
        name: String,
        lastName: String,
        completion: (message: String) -> Unit,
        onError: (errorCode: Int, message: String) -> Unit
    ) {
        userClient.registerUser(email, password, name, lastName, completion, onError)
    }

    private fun refreshUser(userId: String, status: MutableLiveData<RepositoryStatus<User>>) {
        // Runs in a background thread.
        executor.execute {
            // Check if user data was fetched recently.
            val userExists = userDao.hasUser(userId, FRESH_TIMEOUT)
            if (userExists == null) {
                // Refreshes the data.
                userClient.getUserById(userId, { user ->
                    executor.execute {
                        user.let { userDao.save(it) }
                        status.postValue(RepositoryStatus.Success(user))
                    }
                }, {
                    executor.execute { status.postValue(RepositoryStatus.Error(it)) }
                })
            } else status.postValue(RepositoryStatus.Success(userExists))
        }
    }

    companion object {
        val FRESH_TIMEOUT = TimeUnit.DAYS.toMillis(1)
    }
}
