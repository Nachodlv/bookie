package com.example.bookie.api.routes

import com.android.volley.Request

class Login(var email: String, var password: String) :
    ApiRoute() {

    override val url: String
        get() = "$baseUrl/user/login"


    override val httpMethod: Int
        get() = Request.Method.GET


    override val params: HashMap<String, String>
        get() = hashMapOf("email" to email, "password" to password)

}

class Register(
    private val email: String,
    private val password: String,
    private val name: String,
    private val lastName: String
) : ApiRoute() {

    override val url: String
        get() = "$baseUrl/user/register"
    override val httpMethod: Int
        get() = Request.Method.POST

    override val params: HashMap<String, String>
        get() = hashMapOf(
            "email" to email,
            "password" to password,
            "name" to name,
            "lastName" to lastName
        )
}

class UserById(private val id: String, token: String) : ApiRoute(token) {

    override val url: String
        get() = "$baseUrl/user/$id"

    override val httpMethod: Int
        get() = Request.Method.GET

    override val params: HashMap<String, String>
        get() = HashMap()

}