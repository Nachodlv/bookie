package com.bookie.backend.dto

/**
 * Returned when the rating of a book is requested
 */
data class RatingResponse(val id: String, val rating: Double, val reviewAmount: Int)