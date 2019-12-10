package com.example.bookie.api.client

import android.content.Context
import com.example.bookie.api.routes.Login
import com.example.bookie.models.User
import com.example.bookie.models.toObject

class UserClient(ctx: Context) : ApiClient(ctx) {

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
}