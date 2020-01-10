package com.example.bookie.api.routes

import com.android.volley.Request

class Login(var email: String, var password: String) :
    ApiRoute() {

    override val url: String
        get() = "$baseUrl/login"


    override val httpMethod: Int
        get() = Request.Method.POST


    override val params: HashMap<String, String>
        get() = hashMapOf("email" to email, "password" to password)

}

class UserLogged(token: String) : ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/logged"

    override val httpMethod: Int
        get() = Request.Method.GET

    override val params: HashMap<String, String>
        get() = HashMap()

}