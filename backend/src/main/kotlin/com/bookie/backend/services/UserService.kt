package com.bookie.backend.services

import com.bookie.backend.dto.UserDto
import com.bookie.backend.models.User
import com.bookie.backend.util.BasicCrud
import com.bookie.backend.util.JwtTokenUtil
import com.bookie.backend.util.exceptions.EmailAlreadyExistsException
import com.bookie.backend.util.exceptions.UserNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(val userDao: UserDao,
                  val passwordEncoder: PasswordEncoder,
                  private val tokenUtil: JwtTokenUtil) : BasicCrud<String, User> {

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
    fun getByEmail(email: String): Optional<User> = userDao.findByEmail(email)

    /**
     * Registers a new user.
     */
    fun registerUser(user: UserDto): User {
        getByEmail(user.email).ifPresent{throw EmailAlreadyExistsException("Email already exists")}
        val newUser = User(
                user.firstName,
                user.lastName,
                user.email,
                passwordEncoder.encode(user.password))
        return userDao.insert(newUser.apply {}) // Is the apply necessary?
    }

    /**
     * The currently logged in user starts to follow the user with the provided id.
     *
     * The new follower will be added to the followed user's list.
     * The followed user will be added to the current user's following list.
     *
     * @param token: The token of the currently logged in user, received in the request.
     * @param id: The string of the user to be followed.
     */
    fun followUser(token: String, id: String): User {

        val email = tokenUtil.getUsernameFromToken(token)
        val loggedUser: User = getByEmail(email).get()

        val followedUser: User = getById(id).orElseThrow{UserNotFoundException("The user to be followed was not found")}

        followedUser.addFollower(loggedUser)
        update(loggedUser)
        update(followedUser)

        return followedUser
    }

    /**
     * The currently logged in user stops following the user with the provided id.
     *
     * The follower will be removed from the followed user's list.
     * The followed user will be removed from the current user's following list.
     *
     * @param token: The token of the currently logged in user, received in the request.
     * @param id: The string of the user to be unfollowed.
     */
    fun unfollowUser(token: String, id: String): User {

        val email = tokenUtil.getUsernameFromToken(token)
        val loggedUser: User = getByEmail(email).get()

        val followedUser: User = getById(id).orElseThrow{UserNotFoundException("The user to be followed was not found")}

        followedUser.removeFollower(loggedUser)
        update(loggedUser)
        update(followedUser)

        return followedUser
    }
}
