package com.example.bookie.api.client

import android.content.Context
import android.content.SharedPreferences
import com.example.bookie.R
import com.example.bookie.api.routes.ReviewPost
import com.example.bookie.dao.SharedPreferencesDao
import com.example.bookie.models.ReviewResponse
import com.example.bookie.models.toObject

class ReviewClient(ctx: Context?) : ApiClient(ctx) {

    fun postReview(
        bookId: String,
        comment: String,
        score: Int,
        completion: (review: ReviewResponse) -> Unit,
        error: (errorMessage: String) -> Unit
    ) {
        if(ctx == null) return
        val token = SharedPreferencesDao.getToken(ctx)
        if(token == null) {
            error(ctx.getString(R.string.default_error))
            return
        }
        val route = ReviewPost(bookId, comment, score, token)
        performRequest(route) { response ->
            when (response.statusCode) {
                200 -> completion(response.json.toObject())
                else -> error(response.json)
            }
        }
    }
}