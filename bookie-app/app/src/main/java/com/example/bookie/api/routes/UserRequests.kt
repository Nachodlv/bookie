package com.example.bookie.api.routes

import com.android.volley.Request

class Register(
    private val email: String,
    private val password: String,
    private val firstName: String,
    private val lastName: String
) : ApiRoute() {

    override val url: String
        get() = "$baseUrl/user/register"
    override val httpMethod: Int
        get() = Request.Method.POST

    override val params: HashMap<String, Any>
        get() = hashMapOf(
            "email" to email,
            "password" to password,
            "firstName" to firstName,
            "lastName" to lastName
        )
}

class UserById(private val id: String, token: String) : ApiRoute(token) {

    override val url: String
        get() = "$baseUrl/user/$id"

    override val httpMethod: Int
        get() = Request.Method.GET

    override val params: HashMap<String, Any>
        get() = HashMap()

}

class UserReviews(
    private val id: String,
    private val page: Int,
    private val size: Int,
    token: String
) : ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/user/reviews/$id?page=$page&size=$size"
    override val httpMethod: Int
        get() = Request.Method.GET
    override val params: HashMap<String, Any>
        get() = hashMapOf()
}

class UserFollowers(
    private val id: String,
    private val page: Int,
    private val size: Int,
    token: String
) : ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/user/followers/$id?page=$page&size$size"
    override val httpMethod: Int
        get() = Request.Method.GET
    override val params: HashMap<String, Any>
        get() = hashMapOf()
}

class UserFollowing(
    private val id: String,
    private val page: Int,
    private val size: Int,
    token: String
): ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/user/following/$id?page=$page&size$size"
    override val httpMethod: Int
        get() = Request.Method.GET
    override val params: HashMap<String, Any>
        get() = hashMapOf()

}

class UserSearch(private val query: String, private val page: Int, private val size: Int, token: String): ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/user/search?q=$query&page=$page&size=$size"
    override val httpMethod: Int
        get() = Request.Method.GET
    override val params: HashMap<String, Any>
        get() = hashMapOf()

}

class UserFeed(private val size: Int, token: String): ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/user/feed?size=$size"
    override val httpMethod: Int
        get() = Request.Method.GET
    override val params: HashMap<String, Any>
        get() = hashMapOf()

}