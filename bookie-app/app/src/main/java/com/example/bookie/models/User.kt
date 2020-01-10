package com.example.bookie.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "user")
data class User(
    @PrimaryKey @SerializedName("id") @ColumnInfo(name = "id") val id: Long,
    @SerializedName("email") @ColumnInfo(name = "email") val email: String,
    @SerializedName("firstName") @ColumnInfo(name = "firstName") val firstName: String,
    @SerializedName("lastName") @ColumnInfo(name = "lastName") val lastName: String,
    @ColumnInfo(name = "lastFetch") val lastFetch: Long = Calendar.getInstance().timeInMillis
) : JSONConvertable

data class UserRegisterForm(
    @SerializedName("email") val email: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("password") val password: String = ""
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