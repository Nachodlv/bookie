package com.example.bookie.api.routes

import com.android.volley.Request

abstract class BooksAPIRoute :
    ApiRoute(baseUrl = "https://www.googleapis.com/books/v1")

class SearchRecommendation(private val query: String, private val limitation: Int) :
    BooksAPIRoute() {
    override val url: String
        get() = "$baseUrl/volumes?q=$query&maxResults=$limitation" //TODO encode
    override val httpMethod: Int
        get() = Request.Method.GET
    override val params: HashMap<String, String>
        get() = hashMapOf()

}