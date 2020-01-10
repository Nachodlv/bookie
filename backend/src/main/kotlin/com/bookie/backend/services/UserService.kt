package com.bookie.backend.services

import com.bookie.backend.dto.FollowResponse
import com.bookie.backend.dto.UserData
import com.bookie.backend.dto.UserDto
import com.bookie.backend.models.User
import com.bookie.backend.util.BasicCrud
import com.bookie.backend.util.JwtTokenUtil
import com.bookie.backend.util.exceptions.EmailAlreadyExistsException
import com.bookie.backend.util.exceptions.SelfFollowingException
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
     * Returns some of a user's data.
     *
     * @param id: The id of the user
     */
    fun getUserDataById(id: String): Optional<UserData> = userDao.findUserDataById(id)

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

        if (loggedUser.id == id) throw SelfFollowingException("You can't follow yourself")

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

    /**
     * Gets the followers for a specific user.
     *
     * Information is also provided about whether the current user is following each of the users in the result list or not.
     *
     * @param id: The id of the user whose followers will be returned
     * @param page: The page number
     * @param size: The size of the page
     * @param token: The authorization token of the current user
     */
    fun getFollowers(id: String, page: Int, size: Int, token: String): List<FollowResponse> {
        val email = tokenUtil.getUsernameFromToken(token)
        val following = userDao.findAllFollowingByEmail(email).following

        val followerList: List<FollowResponse> = userDao.findFollowersById(id, page * size, size).followers

        checkFollowing(followerList, following)
        return followerList
    }

    /**
     * Gets the users that a specific user follows.
     *
     * Information is also provided about whether the current user is following each of the users in the result list or not.
     *
     * @param id: The id of the user
     * @param page: The page number
     * @param size: The size of the page
     * @param token: The authorization token of the current user
     */
    fun getFollowing(id: String, page: Int, size: Int, token: String): List<FollowResponse> {
        val email = tokenUtil.getUsernameFromToken(token)
        val following = userDao.findAllFollowingByEmail(email).following

        val followerList: List<FollowResponse> = userDao.findFollowingById(id, page * size, size).following

        checkFollowing(followerList, following)
        return followerList
    }

    /**
     * For each item in result, we check if there is an item in following with the same id.
     * If there is, then the followed value in the result item is set to true.
     *
     * @param result: The follower list to be returned
     * @param following: A list with the users that the current user follows
     */
    private fun checkFollowing(result: List<FollowResponse>, following: List<FollowResponse>) {
        result.map{
            item ->
            run {
                if (following.firstOrNull { entry -> entry.id == item.id } !== null) {
                    item.followed = true
                    print("test")
                }
            }
        }

    }
}
