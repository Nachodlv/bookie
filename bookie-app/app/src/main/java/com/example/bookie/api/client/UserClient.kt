package com.example.bookie.api.client

import android.content.Context
import com.example.bookie.R
import com.example.bookie.api.routes.Login
import com.example.bookie.api.routes.Register
import com.example.bookie.api.routes.UserById
import com.example.bookie.models.User
import com.example.bookie.models.toObject


class UserClient(ctx: Context?) : ApiClient(ctx) {
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

    /**
     * --------- REGISTER USER ----------------------------
     **/
    fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        completion: (message: String) -> Unit,
        onError: (errorCode: Int, message: String) -> Unit
        ) {
        if (ctx == null) return
        val route = Register(email, password, firstName, lastName)
        this.performRequest(route) { response ->
            when (response.statusCode) {
                200 -> {
                    completion.invoke(ctx.getString(R.string.register_successful))
                }
                409 -> {
                    onError.invoke(409, ctx.getString(R.string.email_repeated))
                }
                else -> {
                    onError.invoke(
                        response.statusCode,
                        ctx.getString(R.string.register_unsuccessful)
                    )
                }
            }
        }
    }

    //
    fun getUserById(id: String, completion: (user: User?, message: String) -> Unit) {
//        TODO get token in shared preferences
        val route = UserById(id, "")
        this.performRequest(route) { response ->
            if (response.statusCode == 200) {
                val user: User = response.json.toObject()
                completion.invoke(user, "User gotten successfully")
            } else {
                completion.invoke(null, "Error getting the user")
            }
        }
    }
}