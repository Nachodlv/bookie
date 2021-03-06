package com.example.bookie.models

import com.google.gson.annotations.SerializedName
import java.util.*

open class ReviewTab(
        @SerializedName("id") val id: String,
        val userId: String,
        @SerializedName("title") val title: String,
        @SerializedName("preview") val preview: String,
        @SerializedName("image") val image: String?,
        @SerializedName("rating") val rating: Float,
        @SerializedName("likes") var likes: Int,
        @SerializedName("time") val time: Date,
        @SerializedName("isLiked") val isLiked: Boolean = false

) : JSONConvertable

class EmptyReviewTab(@SerializedName("book") val book: Book
) : ReviewTab("", "", "", "", null, 0f, 0, Date())