package com.example.bookie.api.client

import android.content.Context
import com.example.bookie.R
import com.example.bookie.api.routes.BookById
import com.example.bookie.api.routes.IsbnSearch
import com.example.bookie.api.routes.BookSearch
import com.example.bookie.models.Book
import com.example.bookie.models.toObject
import org.json.JSONArray
import org.json.JSONObject

class BookApiClient(ctx: Context?) : ApiClient(ctx) {
    fun searchBook(
        query: String,
        limitation: Int,
        index: Int,
        completion: (books: List<Book>) -> Unit,
        error: (errorMessage: String) -> Unit
    ) {
        if (ctx == null) return

        val route = BookSearch(query, limitation, index)
        this.performRequest(route) { response ->
            when (response.statusCode) {
                200 -> {
                    val result = response.json
                    completion.invoke(fromJsonToBooks(result))
                }
                else -> {
                    error(ctx.getString(R.string.default_error))
                }
            }
        }
    }

    fun searchByIsbn(
        isbn: String,
        completion: (book: Book) -> Unit,
        error: (errorMessage: String) -> Unit
    ) {
        if (ctx == null) return

        val route = IsbnSearch(isbn)
        this.performRequest(route) { response ->
            when (response.statusCode) {
                200 -> {
                    val books = fromJsonToBooks(response.json)
                    if (books.isNotEmpty()) completion(books[0])
                    else error(ctx.getString(R.string.no_book_found))
                }
                else -> {
                    error(ctx.getString(R.string.default_error))
                }
            }
        }
    }

    fun getBookById(id: String, completion: (book: Book) -> Unit, error: (errorMessage: String) -> Unit) {
        if(ctx == null) return

        val route = BookById(id)
        this.performRequest(route) {response ->
            when(response.statusCode) {
                200 -> {
                    val books = fromJsonToBooks(response.json)
                    if(books.isNotEmpty()) completion(books[0])
                    else error(ctx.getString(R.string.default_error))
                } else -> error(ctx.getString(R.string.default_error))
            }
        }

    }

    private fun fromJsonToBooks(json: String): List<Book> {
        val jsonObject = JSONObject(json)
        if((jsonObject["totalItems"] as Int) == 0) return emptyList()
        val booksJson = jsonObject["items"] as JSONArray
        val books = mutableListOf<Book>()
        for (i in 0 until booksJson.length()) {
            val jsonBook = booksJson[i] as JSONObject
            val book: Book = jsonBook["volumeInfo"].toString().toObject()
            book.id = jsonBook["id"] as String
            books.add(book)
        }
        return books
    }
}