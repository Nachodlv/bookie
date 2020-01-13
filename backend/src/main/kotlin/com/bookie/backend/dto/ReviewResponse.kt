package com.bookie.backend.dto

import com.bookie.backend.models.Author
import java.time.Instant

data class ReviewResponse(val rating: Int,
                          val comment: String,
                          val author: Author?,
                          val timestamp: Instant,
                          val id: String,
                          val likes: Int,
                          val liked: Boolean = false)