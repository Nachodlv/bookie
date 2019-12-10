package com.example.bookie.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String
) : JSONConvertable

/*
*
//From JSON
val json = "..."
val object = json.toObject<User>()

// To JSON
val json = object.toJSON()
*
* */