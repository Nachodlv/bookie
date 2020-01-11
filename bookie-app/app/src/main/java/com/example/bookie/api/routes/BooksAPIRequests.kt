package com.example.bookie.api.routes

import com.android.volley.Request

abstract class BooksAPIRoute :
    ApiRoute(baseUrl = "https://www.googleapis.com/books/v1") {
    override val httpMethod: Int
        get() = Request.Method.GET
    override val params: HashMap<String, Any>
        get() = hashMapOf()
}

class BookSearch(private val query: String, private val limitation: Int, private val index: Int) :
    BooksAPIRoute() {
    override val url: String
        get() = "$baseUrl/volumes?q=$query&maxResults=$limitation&startIndex=$index" //TODO encodoe
}

class IsbnSearch(private val isbn: String): BooksAPIRoute() {
    override val url: String
        get() = "$baseUrl/volumes?q=isbn:$isbn"
}

class BookById(private val id: String): BooksAPIRoute() {
    override val url: String
    get() = "$baseUrl/volumes?q=id:$id"
}

