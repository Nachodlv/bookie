package com.bookie.backend.services

import com.bookie.backend.models.Schemas
import org.springframework.data.mongodb.repository.MongoRepository

interface CustomerDao: MongoRepository<Schemas, String>