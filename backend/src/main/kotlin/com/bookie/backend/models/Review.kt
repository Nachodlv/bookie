package com.bookie.backend.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import java.time.Instant

/**
 * Represents a book review
 */
data class Review(val rating: Int, // Score is a whole number
                  val comment: String,
                  val author: Author,
                  val timestamp: Instant,
                  @Id val id: String) // Check what this is and how it behaves.