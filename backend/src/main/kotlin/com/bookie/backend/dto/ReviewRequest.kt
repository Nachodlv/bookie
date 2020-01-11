package com.bookie.backend.dto

data class ReviewRequest(val id: String,
                         val comment: String,
                         val score: Int)