package com.example.bookie.models

import com.google.gson.annotations.SerializedName

data class BookSearch(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("author") val author: String
) : JSONConvertable

data class BookFeed(
        @SerializedName("id") val id: String,
        @SerializedName("title") val title: String,
        @SerializedName("author") val author: String,
        @SerializedName("image") val image: String,
        @SerializedName("rating") val rating: Float
) : JSONConvertable