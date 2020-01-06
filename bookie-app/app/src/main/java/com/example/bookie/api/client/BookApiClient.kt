package com.example.bookie.api.client

import android.content.Context
import com.example.bookie.R
import com.example.bookie.api.routes.SearchRecommendation
import com.example.bookie.models.Book
import com.example.bookie.models.toObject
import org.json.JSONArray
import org.json.JSONObject

class BookApiClient(ctx: Context?) : ApiClient(ctx) {
    fun searchRecommendation(
        query: String,
        limitation: Int,
        completion: (books: List<Book>) -> Unit,
        error: (errorMessage: String) -> Unit
    ) {
        if (ctx == null) return

        val route = SearchRecommendation(query, limitation)
        this.performRequest(route) { response ->
            when (response.statusCode) {
                200 -> {
                    val result = response.json
                    val booksJson = JSONObject(result)["items"] as JSONArray
                    val books = mutableListOf<Book>()
                    for (i in 0 until booksJson.length()) {
                        val jsonBook = booksJson[i] as JSONObject
                        val book: Book = jsonBook["volumeInfo"].toString().toObject()
                        book.id = jsonBook["id"] as String
                        books.add(book)
                    }
                    completion.invoke(books)
                }
                else -> {
                    error(ctx.getString(R.string.default_error))
                }
            }
        }
    }
}