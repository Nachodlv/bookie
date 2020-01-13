package com.example.bookie.api.client

import android.content.Context
import com.example.bookie.R
import com.example.bookie.api.routes.Register
import com.example.bookie.api.routes.UserById
import com.example.bookie.models.User
import com.example.bookie.models.toObject


class UserClient(ctx: Context?) : ApiClient(ctx) {

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