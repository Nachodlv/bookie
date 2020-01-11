package com.bookie.backend.models

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Follower(
        val firstName: String,
        val lastName: String,
        val id: String?
)