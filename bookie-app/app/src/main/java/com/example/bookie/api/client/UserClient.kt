package com.example.bookie.api.client

import android.content.Context
import com.example.bookie.R
import com.example.bookie.api.routes.Register
import com.example.bookie.api.routes.UserById
import com.example.bookie.models.User
import com.example.bookie.models.toObject
import com.example.bookie.api.routes.*
import com.example.bookie.dao.SharedPreferencesDao
import com.example.bookie.models.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception


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
    fun getUserById(
        id: String,
        completion: (user: User) -> Unit,
        error: (message: String) -> Unit
    ) {
        if (ctx == null) return
        val token = SharedPreferencesDao.getToken(ctx)
        if (token == null) {
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
        if (ctx == null) return
        val token = SharedPreferencesDao.getToken(ctx)
        if (token == null) {
            error(ctx.getString(R.string.default_error))
            return
        }
        val route = UserReviews(userId, page, size, token)
        performRequest(route) {
            when (it.statusCode) {
                200 -> {
                    val array = JSONArray(it.json)
                    val reviews = mutableListOf<UserReviewResponse>()
                    for (i in 0 until array.length()) {
                        reviews.add(array[i].toString().toObject())
                    }
                    completion(reviews)
                }
                else -> error(ctx.getString(R.string.default_error))
            }
        }
    }


    fun getUserFollowers(
        userId: String,
        page: Int,
        size: Int,
        completion: (followers: List<UserFollower>) -> Unit,
        error: (errorMessage: String) -> Unit
    ) {
        if (ctx == null) return
        val token = SharedPreferencesDao.getToken(ctx)
        if (token == null) {
            error(ctx.getString(R.string.default_error))
            return
        }
        val route = UserFollowers(userId, page, size, token)
        getFollowers(ctx, route, completion, error)
    }


    fun getUserFollowing(
        userId: String,
        page: Int,
        size: Int,
        completion: (followers: List<UserFollower>) -> Unit,
        error: (errorMessage: String) -> Unit
    ) {
        if (ctx == null) return
        val token = SharedPreferencesDao.getToken(ctx)
        if (token == null) {
            error(ctx.getString(R.string.default_error))
            return
        }
        val route = UserFollowing(userId, page, size, token)
        getFollowers(ctx, route, completion, error)
    }

    fun searchUser(
        query: String,
        page: Int,
        size: Int,
        completion: (users: List<UserFollower>) -> Unit,
        error: (errorMessage: String) -> Unit
    ) {
        if (ctx == null) return
        val token = SharedPreferencesDao.getToken(ctx)
        if (token == null) {
            error(ctx.getString(R.string.default_error))
            return
        }
        val route = UserSearch(query, page, size, token)
        getFollowers(ctx, route, completion, error)
    }

    fun getFeed(
        size: Int,
        completion: (feed: List<FeedResponse>) -> Unit,
        error: (errorMessage: String) -> Unit
    ) {
        if (ctx == null) return
        val token = SharedPreferencesDao.getToken(ctx)
        if (token == null) {
            error(ctx.getString(R.string.default_error))
            return
        }
        val route = UserFeed(size, token)
        performRequest(route) {
            when (it.statusCode) {
                200 -> {
                    val array = JSONArray(it.json)
                    val reviews = mutableListOf<FeedResponse>()
                    for (i in 0 until array.length()) {
                        reviews.add(
                            when (JSONObject(array[i].toString())["type"] as Int) {
                                FeedItemType.REVIEW.id -> array[i].toString().toObject<ReviewFeedResponse>()
                                FeedItemType.BOOK.id -> array[i].toString().toObject<BookFeedResponse>()
                                else -> throw Exception()
                            }
                        )
                    }
                    completion(reviews)
                }
                else -> error(it.json)
            }
        }
    }

    private fun getFollowers(
        context: Context,
        route: ApiRoute,
        completion: (followers: List<UserFollower>) -> Unit,
        error: (errorMessage: String) -> Unit
    ) {

        performRequest(route) { response ->
            when (response.statusCode) {
                200 -> {
                    val array = JSONArray(response.json)
                    val reviews = mutableListOf<UserFollower>()
                    for (i in 0 until array.length()) {
                        reviews.add(array[i].toString().toObject())
                    }
                    completion(reviews)
                }
                else -> error(context.getString(R.string.default_error))
            }
        }
    }

    private inline fun <reified T : JSONConvertable> getModelsFromArray(json: String): List<T> {
        val array = JSONArray(json)
        val models = mutableListOf<T>()
        for (i in 0 until array.length()) {
            models.add(array[i].toString().toObject())
        }
        return models
    }

}