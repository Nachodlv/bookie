package com.example.bookie.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

data class BookImage(
    @SerializedName("smallThumbnail") val smallThumbnail: String,
    @SerializedName("thumbnail") val thumbnail: String
) : JSONConvertable

@Entity(tableName = "book")
data class Book(
    @PrimaryKey @SerializedName("id") var id: String,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String?,
    @SerializedName("authors") val authors: List<String>?,
    @SerializedName("publisher") val publisher: String?,
    @SerializedName("imageLinks") val imageLinks: BookImage?,
    @SerializedName("previewLink") val previewLink: String?,
    @SerializedName("categories") val categories: List<String>?,
    @ColumnInfo(name = "lastFetch") var lastFetch: Long = Calendar.getInstance().timeInMillis,
    var review: Rating?
) : JSONConvertable {
    fun toBookFeed(): BookFeed {
        return BookFeed(
            id,
            title,
            authors?.reduce { a, b -> "$a, $b" },
            imageLinks?.thumbnail,
            review?.rating
        )
    }
}

data class Rating(
    @SerializedName("rating") var rating: Float,
    @SerializedName("reviewAmount") var reviewAmount: Int
) : JSONConvertable