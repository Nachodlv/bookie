package com.example.bookie.api.client

import android.content.Context
import com.example.bookie.R
import com.example.bookie.api.routes.Login
import com.example.bookie.api.routes.UserLogged
import com.example.bookie.dao.SharedPreferencesDao
import com.example.bookie.models.User
import com.example.bookie.models.toObject

class AuthClient(ctx: Context?, val sharedPreferencesDao: SharedPreferencesDao) : ApiClient(ctx) {
    /**
     * --------- LOGIN USER ----------------------------
     **/
    fun loginUser(
        email: String,
        password: String,
        completion: (token: String, message: String) -> Unit,
        onError: (errorCode: Int, message: String) -> Unit
    ) {
        if (ctx == null) return

        val route = Login(email, password)
        this.performRequest(route) { response ->
            when (response.statusCode) {
                200 -> {
                    val token = response.json
                    completion.invoke(token, ctx.getString(R.string.login_successful))
                }
                401 -> {
                    onError.invoke(401, ctx.getString(R.string.invalid_credentials))
                }
                else -> {
                    onError.invoke(400, ctx.getString(R.string.login_unsuccessful))
                }
            }
        }
    }

    fun getUserLoggedIn(
        completion: (user: User) -> Unit,
        onError: (errorCode: Int, message: String) -> Unit
    ) {
        if (ctx == null) return
        val token = sharedPreferencesDao.getToken() ?: return
        val route = UserLogged(token)
        this.performRequest(route) { response ->
            when(response.statusCode) {
                200 -> {
                    val user = response.json.toObject<User>()
                    completion(user)
                }
                else -> {
                    onError(response.statusCode, ctx.getString(R.string.error_getting_user))
                }
            }
        }

    }
}