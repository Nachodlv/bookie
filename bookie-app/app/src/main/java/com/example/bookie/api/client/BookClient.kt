package com.example.bookie.api.client

import android.content.Context
import com.example.bookie.R
import com.example.bookie.api.routes.BookRating
import com.example.bookie.api.routes.MultipleBookRatings
import com.example.bookie.dao.SharedPreferencesDao
import com.example.bookie.models.Rating
import com.example.bookie.models.toObject
import com.google.gson.Gson

class BookClient(ctx: Context?) : ApiClient(ctx) {

    fun getRating(bookId: String, completion: (Rating) -> Unit, error: (String) -> Unit) {

        if (ctx == null) return
        val token = SharedPreferencesDao.getToken(ctx)
        if (token == null) {
            error(ctx.getString(R.string.default_error))
            return
        }

        val route = BookRating(bookId, token)
        this.performRequest(route) { response ->
            when (response.statusCode) {
                200 -> {
                    completion(response.json.toObject())
                }
                else -> {
                    error(ctx.getString(R.string.default_error))
                }
            }
        }
    }

    fun getMultipleRatings(
        booksIds: List<String>,
        completion: (List<Rating>) -> Unit,
        error: (String) -> Unit
    ) {

        if (ctx == null) return
        val token = SharedPreferencesDao.getToken(ctx)
        if (token == null) {
            error(ctx.getString(R.string.default_error))
            return
        }
        val route = MultipleBookRatings(booksIds, token)
        this.performRequest(route){response ->
            when(response.statusCode) {
                200 -> {
                    completion(Gson().fromJson(response.json, List::class.java).map { it.toString().toObject<Rating>() })
                }
                else -> {
                    error(ctx.getString(R.string.default_error))
                }
            }
        }
    }
}
