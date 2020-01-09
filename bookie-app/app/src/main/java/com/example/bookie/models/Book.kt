package com.example.bookie.models

import com.google.gson.annotations.SerializedName

data class BookSearch(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("author") val author: String
) : JSONConvertable