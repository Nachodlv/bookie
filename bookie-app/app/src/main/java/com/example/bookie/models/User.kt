package com.example.bookie.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "user")
data class User(
    @PrimaryKey @SerializedName("id") @ColumnInfo(name = "id") val id: String,
    @SerializedName("email") @ColumnInfo(name = "email") val email: String,
    @SerializedName("firstName") @ColumnInfo(name = "firstName") val firstName: String,
    @SerializedName("lastName") @ColumnInfo(name = "lastName") val lastName: String,
    @SerializedName("followerAmount") val followerAmount: Int,
    @ColumnInfo(name = "lastFetch") var lastFetch: Long = Calendar.getInstance().timeInMillis
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

data class UserReviewResponse(
    @SerializedName("id") val id: String,
    @SerializedName("comment") val comment: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("timestamp") val timestamp: Date
) : JSONConvertable {
    fun toReview(user: User): Review = Review(
        id,
        user.id,
        user.firstName,
        user.lastName,
        comment,
        rating,
        timestamp,
        Calendar.getInstance().timeInMillis
    )
}

data class UserFollower(
    @SerializedName("id") val id: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("followed") val followed: Boolean
) : JSONConvertable {
    fun toUserPreview(isFollower: Boolean): UserPreview =
        UserPreview(id, firstName, lastName, isFollower, followed)

}