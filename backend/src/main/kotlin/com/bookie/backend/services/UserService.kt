package com.bookie.backend.services

import com.bookie.backend.dto.FollowResponse
import com.bookie.backend.dto.UserData
import com.bookie.backend.dto.UserDto
import com.bookie.backend.models.*
import com.bookie.backend.util.BasicCrud
import com.bookie.backend.util.JwtTokenUtil
import com.bookie.backend.util.exceptions.EmailAlreadyExistsException
import com.bookie.backend.util.exceptions.SelfFollowingException
import com.bookie.backend.util.exceptions.UserNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
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
        getByEmail(user.email).ifPresent { throw EmailAlreadyExistsException("Email already exists") }
        val newUser = User(
                user.firstName,
                user.lastName,
                user.email,
                passwordEncoder.encode(user.password))
        return userDao.insert(newUser.apply {}) // Is the apply necessary?
    }

    fun resetUserPassword(token: String, newPassword: String) {
        val email = tokenUtil.getUsernameFromToken(token)
        changeUserPassword(email, newPassword)
    }

    /**
     * Changes a user's password to the one provided as method parameter.
     */
    fun changeUserPassword(email: String, newPassword: String) {
        val user: User = getByEmail(email).orElseThrow{ throw UserNotFoundException("No user with that id was found.") }
        user.password = passwordEncoder.encode(newPassword)
        update(user)
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

        val followedUser: User = getById(id).orElseThrow { UserNotFoundException("The user to be followed was not found") }

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

        val followedUser: User = getById(id).orElseThrow { UserNotFoundException("The user to be followed was not found") }

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

        val result = userDao.findFollowersById(id, page * size, size)
        if (!result.isPresent) throw UserNotFoundException("No user found with that id")

        val followerList: List<FollowResponse> = result.get().followers

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

        val result = userDao.findFollowingById(id, page * size, size)
        if (!result.isPresent) throw UserNotFoundException("No user found with that id")

        val followerList: List<FollowResponse> = result.get().following

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
        result.map { item ->
            run {
                if (following.firstOrNull { entry -> entry.id == item.id } !== null) {
                    item.followed = true
                    print("test")
                }
            }
        }

    }

    /**
     * Returns the user for the token provided.
     */
    fun getByToken(token: String): Optional<User> {
        val email = tokenUtil.getUsernameFromToken(token)
        return getByEmail(email)
    }

    /**
     * Returns the reviews written by a specific user.
     */
    fun getReviews(id: String, page: Int, size: Int): List<Review> {
        val result = userDao.findReviewsById(id, page * size, size)
        if (result.isPresent) {
            return result.get().reviews
        }
        return emptyList()
    }

    /**
     * Returns a user's feed.
     */
    fun getFeed(token: String, size: Int): List<FeedItem> {

        val email = tokenUtil.getUsernameFromToken(token)
        val user: Optional<User> = userDao.findByEmail(email)
        if (user.isPresent) {
            val result = user.get().getLatestFeedItems(size)
            update(user.get())
            return result
        }
        throw UserNotFoundException("No user found with the provided id.")
    }

    /**
     * Adds a review feed item to a user's feed.
     *
     * This method might also add a book feed item to a user's feed, if certain conditions are met.
     *
     * The conditions are the following:
     * - The review must have a rating equal or greater to 3
     * - The book's rating must be equal or greater to 3
     * - The user must not have written a review for the book already
     */
    fun addFeedItems(review: Review, reviewer: User, book: Book) {
        val reviewFeedItem: FeedItem = ReviewFeedItem(review.id, 2, Instant.now(), review.rating)

        val bookFeedItem: FeedItem = BookFeedItem(book.id, 0, book.rating)

        reviewer.followers.forEach { follower ->
            run {
                if (shouldRecommendBook(review, book)) {
                    addReviewAndBookToFeed(reviewFeedItem, bookFeedItem, follower.id)
                } else {
                    addReviewToFeed(reviewFeedItem, follower.id)
                }
            }
        }
    }

    // Add documentation
    fun searchUsers(q: String, token: String, pageable: Pageable): List<UserData> {
        // Should sanitize the query
        val email = tokenUtil.getUsernameFromToken(token)
        val user = userDao.findByEmail(email).get()

        val spaces = q.contains(" ")
        val query = if (spaces) {
            "(" + q.replace(' ', '|') + ")"
        } else {
            q
        }
        val result = userDao.findUsersByQueryParameter(query, user.id!!, pageable)
        return result.orElse(emptyList())
    }

    private fun addReviewToFeed(review: FeedItem, id: String?) {
        if (id != null) {
            val user = userDao.findById(id).get()
            user.addFeedItem(review)
            update(user)
        }
    }

    private fun addReviewAndBookToFeed(review: FeedItem, book: FeedItem, id: String?) {
        if (id != null) {
            val user = userDao.findById(id).get()
            user.addFeedItem(review)
            if (user.reviews.firstOrNull{ item -> item.id == book.id } == null) {
                user.addFeedItem(book)
            }
            update(user)
        }
    }

    private fun shouldRecommendBook(review: Review, book: Book) = book.rating >= 3.0 && review.rating >= 3
}
