package com.bookie.backend.services

import com.bookie.backend.models.User
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface UserDao: MongoRepository<User, String> {

    fun findByEmail(email: String): Optional<User> // Is the return type correct or should it be a list?
}