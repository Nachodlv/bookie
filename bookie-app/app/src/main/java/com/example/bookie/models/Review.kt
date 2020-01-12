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
    @SerializedName("rating") val rating: Float,
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
            "",
            rating,
            timestamp,
            Calendar.getInstance().timeInMillis
        )
    }
}

@Entity(tableName = "review", primaryKeys = ["bookId", "userId"])
data class Review(
    val bookId: String,
    val userId: String,
    val userFirstName: String,
    val userLastName: String,
    val comment: String,
    val score: Float,
    val timestamp: Date,
    val lastFetch: Long = Calendar.getInstance().timeInMillis
) {
    fun toReviewTab(): ReviewTab {
        return ReviewTab(bookId, "$userFirstName $userLastName", comment, null, score, 0, timestamp)
    }
}