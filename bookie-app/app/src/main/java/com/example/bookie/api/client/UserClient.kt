package com.example.bookie.api.client

import android.content.Context
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
        completion: (user: User?, message: String) -> Unit
    ) {
        val route = Login(email, password)
        this.performRequest(route) { success, response ->
            if (success) {
                val user: User = response.json.toObject()
                completion.invoke(user, "Log in successful")
            } else {
                completion.invoke(null, response.message)
            }
        }
    }

    /**
     * --------- REGISTER USER ----------------------------
     **/
    fun registerUser(
        email: String,
        password: String,
        name: String,
        lastName: String,
        completion: (user: User?, message: String) -> Unit
    ) {
        val route = Register(email, password, name, lastName)
        this.performRequest(route) { success, response ->
            if (success) {
                val user: User = response.json.toObject()
                completion.invoke(user, "Register successful")
            } else {
                completion.invoke(null, response.message)
            }
        }
    }

    fun getUserById(id: String, completion: (user: User?, message: String) -> Unit) {
//        TODO get token in shared preferences
        val route = UserById(id, "")
        this.performRequest(route) { success, response ->
            if (success) {
                val user: User = response.json.toObject()
                completion.invoke(user, "User gotten successfully")
            } else {
                completion.invoke(null, response.message)
            }
        }
    }
}