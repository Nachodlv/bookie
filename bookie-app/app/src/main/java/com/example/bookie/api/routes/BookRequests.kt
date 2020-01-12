package com.example.bookie.api.routes

import com.android.volley.Request
import com.example.bookie.models.JSONConvertable
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject

class BookRating(private val id: String, token: String) : ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/book/rating/$id"
    override val httpMethod: Int
        get() = Request.Method.GET
    override val params: HashMap<String, Any>
        get() = hashMapOf()
}

class MultipleBookRatings(private val ids: List<String>, token: String): ApiRoute(token) {
    override val url: String
        get() = "$baseUrl/book/rating"
    override val httpMethod: Int
        get() = Request.Method.PUT
    override val params: HashMap<String, Any>
        get() {
            val array = JSONArray()
            ids.forEach { array.put(it) }
            return hashMapOf("ids" to array)
        }

}

data class RatingRequest(@SerializedName("ids") val ids: List<String>): JSONConvertable