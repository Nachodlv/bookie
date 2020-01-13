package com.example.bookie.api.routes

abstract class ApiRoute(
    private var token: String? = null,
    val baseUrl: String = "http://10.0.2.2:8080"
) {

    val timeOut = 3000

    abstract val url: String

    abstract val httpMethod: Int

    abstract val params: HashMap<String, Any>

    val headers: HashMap<String, String>
        get() {
            val map: HashMap<String, String> = hashMapOf()
            map["Content-Type"] = "application/json"
            if (token != null) map["Authorization"] = "Bearer $token"
            return map
        }

}