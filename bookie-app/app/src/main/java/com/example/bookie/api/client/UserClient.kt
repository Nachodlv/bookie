package com.example.bookie.api.client

import android.content.Context
import com.example.bookie.R
import com.example.bookie.api.routes.Login
import com.example.bookie.api.routes.Register
import com.example.bookie.api.routes.UserById
import com.example.bookie.models.User
import com.example.bookie.models.toObject
import kotlinx.android.synthetic.main.login_main.*


class UserClient(ctx: Context?) : ApiClient(ctx) {
    /**
     * --------- LOGIN USER ----------------------------
     **/
    fun loginUser(
        email: String,
        password: String,
        completion: (user: User?, message: String) -> Unit
    ) {
        val route = Login(email, password)
        this.performRequest(route) { response ->
            if (response.statusCode == 200) {
                val user: User = response.json.toObject()
                completion.invoke(user, "Log in successful")
            } else {
                completion.invoke(null, "Error login in")
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
        completion: (user: User?, message: String) -> Unit
    ) {
        if(ctx == null) return
        val route = Register(email, password, firstName, lastName)
        this.performRequest(route) { response ->
            when (response.statusCode) {
                200 -> {
                    val user: User = response.json.toObject()
                    completion.invoke(user, ctx.getString(R.string.register_successful))
                }
                409 -> {
                    completion.invoke(null, ctx.getString(R.string.email_repeated))
                }
                else -> {
                    completion.invoke(null, ctx.getString(R.string.register_unsuccessful))
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