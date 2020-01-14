package com.bookie.backend.models

import com.bookie.backend.util.exceptions.LikeException
import java.time.Instant

/**
 * Represents a book review
 */
data class Review(val rating: Int, // Score is a whole number
                  val comment: String,
                  val author: Author?, // It can be null
                  val timestamp: Instant,
                  val id: String,
                  var likes: Int = 0,
                  val likedBy: MutableList<String> = mutableListOf()) {

    /**
     * Adds a like to the review
     */
    fun addLike(userId: String) {
        if (this.likedBy.contains(userId)) throw LikeException("Review already liked")
        this.likedBy.add(userId)
        this.likes++
    }

    /**
     * Removes a like from a review
     */
    fun removeLike(userId: String) {
        if (!this.likedBy.contains(userId)) throw LikeException("Review not liked")
        this.likedBy.remove(userId)
        this.likes--
    }
}