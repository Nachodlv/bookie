package com.bookie.backend.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Customer(@Id val id: String? = null, val firstName: String, val lastName: String)