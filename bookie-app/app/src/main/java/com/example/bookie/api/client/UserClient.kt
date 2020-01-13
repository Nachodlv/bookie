package com.example.bookie.api.client

import android.content.Context
import com.example.bookie.R
import com.example.bookie.api.routes.Register
import com.example.bookie.api.routes.UserById
import com.example.bookie.api.routes.UserReviews
import com.example.bookie.dao.SharedPreferencesDao
import com.example.bookie.models.User
import com.example.bookie.models.UserReviewResponse
import com.example.bookie.models.toObject
import org.json.JSONArray


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
    fun getUserById(id: String, completion: (user: User) -> Unit, error: (message: String) -> Unit) {
        if(ctx == null) return
        val token = SharedPreferencesDao.getToken(ctx)
        if(token == null) {
            error(ctx.getString(R.string.default_error))
            return
        }
        val route = UserById(id, token)
        this.performRequest(route) { response ->
            if (response.statusCode == 200) {
                completion.invoke(response.json.toObject())
            } else {
                error.invoke(ctx.getString(R.string.default_error))
            }
        }
    }

    fun getUserReviews(
        userId: String,
        page: Int,
        size: Int,
        completion: (reviews: List<UserReviewResponse>) -> Unit,
        error: (errorMessage: String) -> Unit
    ) {
        if(ctx == null) return
        val token = SharedPreferencesDao.getToken(ctx)
        if(token == null) {
            error(ctx.getString(R.string.default_error))
            return
        }
        val route = UserReviews(userId, page, size, token)
        performRequest(route) {
            when(it.statusCode) {
                200 -> {
                    val array = JSONArray(it.json)
                    val reviews = mutableListOf<UserReviewResponse>()
                    for(i in 0 until array.length()) {
                        reviews.add(array[i].toString().toObject())
                    }
                    completion(reviews)
                }
                else -> error(ctx.getString(R.string.default_error))
            }
        }
    }
}