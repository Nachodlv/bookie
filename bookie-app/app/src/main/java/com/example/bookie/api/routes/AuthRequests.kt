package com.example.bookie.api.routes

import com.android.volley.Request

class Login(var email: String, var password: String) :
    ApiRoute() {

    override val url: String
        get() = "$baseUrl/login"


    override val httpMethod: Int
        get() = Request.Method.POST


    override val params: HashMap<String, Any>
        get() = hashMapOf("email" to email, "password" to password)

}

class UserLogged(token: String) : ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/user/current"

    override val httpMethod: Int
        get() = Request.Method.GET

    override val params: HashMap<String, Any>
        get() = HashMap()

}

class RecoverPassword(private val email: String) : ApiRoute() {
    override val url: String
        get() = "$baseUrl/requestPasswordReset"
    override val httpMethod: Int
        get() = Request.Method.PUT
    override val params: HashMap<String, Any>
        get() = hashMapOf("email" to email) //To change initializer of created properties use File | Settings | File Templates.

}