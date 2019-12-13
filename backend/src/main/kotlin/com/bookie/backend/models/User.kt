package com.bookie.backend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String,
        @Id val id: String? = null,
        val roles: List<String> = emptyList())
