package com.example.bookie.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class ReviewTab(
        @SerializedName("id") val id: String,
        val userId: String,
        @SerializedName("title") val title: String,
        @SerializedName("preview") val preview: String,
        @SerializedName("image") val image: String?,
        @SerializedName("rating") val rating: Float,
        @SerializedName("likes") val likes: Int,
        @SerializedName("time") val time: Date
) : JSONConvertable