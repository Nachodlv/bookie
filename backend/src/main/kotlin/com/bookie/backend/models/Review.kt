package com.bookie.backend.models

import java.time.Instant

/**
 * Represents a book review
 */
data class Review(val rating: Int, // Score is a whole number
                  val comment: String,
                  val author: Author?, // It can be null
                  val timestamp: Instant,
                  val id: String) // The id of the book it was written for (check if this works correctly).