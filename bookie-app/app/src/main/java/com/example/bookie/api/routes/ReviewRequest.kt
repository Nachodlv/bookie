package com.example.bookie.api.routes

import com.android.volley.Request

class ReviewPost(
    private val bookId: String,
    private val comment: String,
    private val score: Int,
    token: String
) : ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/book/review"

    override val httpMethod: Int
        get() = Request.Method.POST

    override val params: HashMap<String, Any>
        get() = hashMapOf("id" to bookId, "comment" to comment, "score" to score)

}

class GetReviews(
    private val bookId: String,
    private val page: Int,
    private val size: Int,
    token: String
) : ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/book/reviews/$bookId?page=$page&size=$size"
    override val httpMethod: Int
        get() = Request.Method.GET
    override val params: HashMap<String, Any>
        get() = hashMapOf()

}