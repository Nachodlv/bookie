package com.example.bookie.api.routes

import com.android.volley.Request
import com.google.gson.Gson

class BookRating(private val id: String, token: String) : ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/book/rating/$id"
    override val httpMethod: Int
        get() = Request.Method.GET
    override val params: HashMap<String, String>
        get() = hashMapOf()
}

class MultipleBookRatings(private val ids: List<String>, token: String): ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/book/multipleRatings"
    override val httpMethod: Int
        get() = Request.Method.GET
    override val params: HashMap<String, String>
        get() = hashMapOf("ids" to Gson().toJson(ids))

}