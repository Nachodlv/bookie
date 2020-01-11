package com.example.bookie.models

import com.google.gson.annotations.SerializedName
import java.util.*

open class FeedItem(
        @SerializedName("id") val id: String,
        @SerializedName("title") val title: String,
        @SerializedName("author") val author: String,
        @SerializedName("image") val image: String,
        @SerializedName("type") val type: FeedItemType
) : JSONConvertable

class BookFeed(
        id: String,
        title: String,
        author: String,
        image: String,
        type: FeedItemType,
        @SerializedName("rating") val rating: Float)
    : FeedItem(id, title, author, image, type)

class FollowerComment(
        id: String,
        title: String,
        author: String,
        image: String,
        type: FeedItemType,
        @SerializedName("preview") val preview: String,
        @SerializedName("time") val time: Date)
    : FeedItem(id, title, author, image, type)

class FollowerReview(
        id: String,
        title: String,
        author: String,
        image: String,
        type: FeedItemType,
        @SerializedName("rating") val rating: Float,
        @SerializedName("time") val time: Date)
    : FeedItem(id, title, author, image, type)

enum class FeedItemType(val id: Int) {
    BOOK(0), COMMENT(1), REVIEW(2)
}