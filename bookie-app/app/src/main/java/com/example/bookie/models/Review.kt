package com.example.bookie.models

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.util.*


data class Author(
    @SerializedName("id") val id: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String
) : JSONConvertable

data class ReviewResponse(
    @SerializedName("comment") val comment: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("id") val id: String,
    @SerializedName("timestamp") val timestamp: Date,
    @SerializedName("author") val author: Author

) : JSONConvertable {

    fun toReview(): Review {
        return Review(
            id,
            author.id,
            author.firstName,
            author.lastName,
            comment,
            rating,
            timestamp,
            Calendar.getInstance().timeInMillis
        )
    }
}

@Entity(tableName = "review", primaryKeys = ["bookId", "userId"])
data class Review(
    val bookId: String,
    var userId: String,
    val userFirstName: String,
    val userLastName: String,
    val comment: String,
    val score: Int,
    val timestamp: Date,
    val lastFetch: Long = Calendar.getInstance().timeInMillis
) {
    fun toReviewTab(): ReviewTab {
        return ReviewTab(
            bookId,
            userId,
            "$userFirstName $userLastName",
            comment,
            null,
            score.toFloat(),
            0,
            timestamp
        )
    }
}