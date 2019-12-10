package com.example.bookie.api.routes

import android.content.Context

abstract class ApiRoute(private var token: String? = null) {

    val timeOut = 3000

    val baseUrl = "http://10.0.2.2:1234"

    abstract val url: String

    abstract val httpMethod: Int

    abstract val params: HashMap<String, String>

    val headers: HashMap<String, String>
        get() {
            val map: HashMap<String, String> = hashMapOf()
            map["Accept"] = "application/json"
            if(token != null) map["Authorization"] = "Bearer $token"
            return map
        }

}