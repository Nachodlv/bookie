package com.example.bookie.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bookie.api.client.AuthClient
import com.example.bookie.dao.SharedPreferencesDao
import com.example.bookie.dao.UserDao
import com.example.bookie.models.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executor

class AuthRepository constructor(
    private val sharedPreferencesDao: SharedPreferencesDao,
    private val authClient: AuthClient,
    private val userDao: UserDao,
    private val executor: Executor
) {
    private val status: MutableLiveData<RepositoryStatus<User>> by lazy {
        val list = MutableLiveData<RepositoryStatus<User>>()
        list.value = RepositoryStatus.Loading()
        list
    }

    fun loginUser(
        email: String,
        password: String,
        completion: (message: String) -> Unit,
        onError: (errorCode: Int, message: String) -> Unit
    ) {
        authClient.loginUser(email, password, { token, message ->
            sharedPreferencesDao.saveToken(token)
            completion(message)
        }, onError)
    }

    fun isUserLogged(): Pair<Boolean, LiveData<RepositoryStatus<User>>?> {
        return if (sharedPreferencesDao.isTokenValid()) {
            Pair(true, getUserLoggedIn())
        } else {
            Pair(false, null)
        }
    }

    fun getUserLoggedIn(): LiveData<RepositoryStatus<User>> {

        val jwt = sharedPreferencesDao.getTokenAsJwt()
        if (jwt == null)
            status.value = RepositoryStatus.Error("Authentication Error")
        else {
            val subject = jwt.subject
            if (subject != null) refreshUser(subject)
        }

        return status
    }

    private fun refreshUser(email: String) {
        // Runs in a background thread.
        executor.execute {
            // Check if user data was fetched recently.
            val userExists = userDao.hasUserByEmail(email, UserRepository.FRESH_TIMEOUT)

            if (userExists == null) {
                authClient.getUserLoggedIn({ user ->
                    executor.execute {
                        user.lastFetch = Calendar.getInstance().timeInMillis
                        userDao.save(user)
                    }
                    status.value = RepositoryStatus.Success(user)
                }, { _, message -> status.value = RepositoryStatus.Error(message) })
            } else GlobalScope.launch {  status.postValue(RepositoryStatus.Success(userExists))}
        }
    }
}