package com.example.bookie.models

import com.google.gson.annotations.SerializedName
import java.util.*

open class FeedItem(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("type") val type: FeedItemType
) : JSONConvertable

class BookFeed(
    id: String,
    title: String,
    author: String?,
    image: String?,
    @SerializedName("rating") val rating: Float?
) : FeedItem(id, title, author, image, FeedItemType.BOOK)

class FollowerComment(
    id: String,
    title: String,
    author: String,
    image: String,
    @SerializedName("preview") val preview: String,
    @SerializedName("time") val time: Date
) : FeedItem(id, title, author, image, FeedItemType.COMMENT)

class FollowerReview(
    id: String,
    title: String,
    author: String,
    image: String?,
    @SerializedName("rating") val rating: Float,
    @SerializedName("time") val time: Date
) : FeedItem(id, title, author, image, FeedItemType.REVIEW)

sealed class FeedResponse: JSONConvertable {
    abstract val id: String
}

class BookFeedResponse(
    override val id: String,
    @SerializedName("rating") val rating: Float
) : FeedResponse()

class ReviewFeedResponse(
    override val id: String,
    @SerializedName("time") val time: Date,
    @SerializedName("rating") val rating: Float,
    @SerializedName("author") val author: Author
) : FeedResponse() {
    fun toFollowerReview(book: Book): FollowerReview =
        FollowerReview(
            id,
            book.title,
            "${author.firstName} ${author.lastName}",
            book.imageLinks?.thumbnail,
            rating,
            time
        )
}

enum class FeedItemType(val id: Int) {
    BOOK(0), COMMENT(1), REVIEW(2)
}