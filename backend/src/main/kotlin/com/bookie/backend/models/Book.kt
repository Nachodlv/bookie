package com.bookie.backend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * A Book with a score and reviews.
 */
@Document
data class Book(
        @Id val id: String, // This id is the same one as the one used by Google
        var score: Double,
        // val reviewAmount: Int
        val reviews: MutableList<Review> = mutableListOf()) {

    /**
     * Adds a new review to the
     */
    fun addReview(review: Review) {
        recalculateScore(review.score.toDouble())
        this.reviews.add(review)
    }

    private fun recalculateScore(score: Double) {
        this.score = ( this.score * this.reviews.size + score ) / (this.reviews.size+1) // Check if this works correctly.
    }
}