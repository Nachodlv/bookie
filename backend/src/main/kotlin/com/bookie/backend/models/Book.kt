package com.bookie.backend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * A Book with a score and reviews.
 */
@Document
data class Book(
        @Id val id: String, // This id is the same one as the one used by Google
        var rating: Double,
        val reviews: MutableList<Review> = mutableListOf(),
        var reviewAmount: Int = 0) {

    /**
     * Adds a new review to the
     */
    fun addReview(review: Review) {
        recalculateRating(review.rating.toDouble())
        this.reviews.add(review)
        this.reviewAmount++
    }

    private fun recalculateRating(rating: Double) {
        this.rating = ( this.rating * reviewAmount + rating ) / (reviewAmount+1)
    }
}