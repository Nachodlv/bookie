package com.bookie.backend.dto

import com.bookie.backend.models.Review

class AnonymousReview(review: Review, liked: Boolean) {
    val rating = review.rating
    val comment = review.comment
    val timestamp = review.timestamp
    val id = review.id
    val likes = review.likes
    val liked = liked
}