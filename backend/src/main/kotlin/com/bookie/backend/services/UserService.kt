package com.bookie.backend.services;

import com.bookie.backend.dto.UserDto
import com.bookie.backend.models.User
import com.bookie.backend.util.BasicCrud
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service;
import java.lang.RuntimeException
import java.util.*

@Service
class UserService(val userDao: UserDao) : BasicCrud<String, User> {

    override fun getAll(pageable: Pageable): Page<User> = userDao.findAll(pageable)

    override fun getById(id: String): Optional<User> = userDao.findById(id)

    override fun insert(obj: User): User = userDao.insert(obj.apply {})

    @Throws(Exception::class)
    override fun update(obj: User): User {
        return if (obj.id != null && userDao.existsById(obj.id)) {
            userDao.save(obj.apply {})
        } else {
            throw object : Exception("Schemas not found") {}
        }
    }

    override fun deleteById(id: String): Optional<User> {
        return userDao.findById(id).apply {
            this.ifPresent { userDao.delete(it) }
        }
    }

    /**
     * Looks for a user with the specified email.
     */
    fun getByEmail(email: String): Optional<User> = userDao.findByEmail(email) // Test if this method works correctly.

    /**
     * Registers a new user.
     */
    fun registerUser(user: UserDto): User {
        // Check if this works correctly.
        getByEmail(user.email).ifPresent{throw RuntimeException("Email already exists")} // Create a custom exception or return an error
        val newUser = User(user.firstName, user.lastName, user.email, user.password)
        return userDao.insert(newUser.apply {}) // Is the apply necessary?
    }
}
