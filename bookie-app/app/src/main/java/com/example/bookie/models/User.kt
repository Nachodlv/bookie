package com.example.bookie.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "user")
data class User(
    @PrimaryKey @SerializedName("id") @ColumnInfo(name = "id") val id: String,
    @SerializedName("email") @ColumnInfo(name = "email") val email: String,
    @SerializedName("firstName") @ColumnInfo(name = "firstName") val firstName: String,
    @SerializedName("lastName") @ColumnInfo(name = "lastName") val lastName: String,
    @ColumnInfo(name = "lastFetch")  var lastFetch: Long = Calendar.getInstance().timeInMillis
) : JSONConvertable

data class UserRegisterForm(
    @SerializedName("email") val email: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("password") val password: String = ""
) : JSONConvertable

data class UserPreview(
        @SerializedName("id") val id: String = "",
        @SerializedName("firstName") val firstName: String = "",
        @SerializedName("lastName") val lastName: String = "",
        @SerializedName("isFollower") val isFollower: Boolean = false,
        @SerializedName("isFollowedByMe") val isFollowedByMe: Boolean = false
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