package com.example.bookie.api

import org.json.JSONObject
import org.json.JSONTokener

class ApiResponse(response: String) {

    var success: Boolean = false
    var message: String = ""
    var json: String = ""

    private val data = "data"
    private val msg = "error"

    init {
        try {
            val jsonToken = JSONTokener(response).nextValue()
            if (jsonToken is JSONObject) {
                val jsonResponse = JSONObject(response)

                message = if (jsonResponse.has(msg)) {
                    jsonResponse.getJSONObject(msg).getString("message")
                } else {
                    "An error was occurred while processing the response"
                }

                if (jsonResponse.optJSONObject(data) != null) {
                    json = jsonResponse.getJSONObject(data).toString()
                    success = true
                } else {
                    success = false
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}