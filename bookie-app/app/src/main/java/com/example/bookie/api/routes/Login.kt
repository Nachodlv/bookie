package com.example.bookie.api.routes

import com.android.volley.Request

class Login(var email: String, var password: String) :
    ApiRoute() {

    override val url: String
        get() = "$baseUrl/login"


    override val httpMethod: Int
        get() = Request.Method.GET


    override val params: HashMap<String, String>
        get() = hashMapOf("email" to email, "password" to password)

}