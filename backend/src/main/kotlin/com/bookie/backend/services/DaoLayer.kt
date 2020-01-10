package com.bookie.backend.services

import com.bookie.backend.dto.FollowResponseList
import com.bookie.backend.dto.FollowingResponseList
import com.bookie.backend.dto.RatingResponse
import com.bookie.backend.dto.UserData
import com.bookie.backend.models.Book
import com.bookie.backend.models.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface UserDao: MongoRepository<User, String> {

    fun findByEmail(email: String): Optional<User>

    @Query(value="{ 'id' : ?0 }")
    fun findUserDataById(id: String): Optional<UserData> // Does this work correctly?

    /**
     * Finds the followers for a specific user.
     *
     * @param id: The id of the user whose followers will be returned
     * @param skip: The amount of followers to skip before the first result (If using page and size, skip would be page * size)
     * @param limit: The maximum amount of followers to be returned
     */
    @Query(value="{ 'id' : ?0 }", fields="{ 'id' : 0, 'followers' : { '\$slice' : [ ?1, ?2 ] } }")
    fun findFollowersById(id: String, skip: Int, limit: Int): FollowResponseList

    /**
     * Finds the users that a specific user is following.
     *
     * @param id: The id of the user
     * @param skip: The amount of followers to skip before the first result (If using page and size, skip would be page * size)
     * @param limit: The maximum amount of followers to be returned
     */
    @Query(value="{ 'id' : ?0 }", fields="{ 'id' : 0, 'following' : { '\$slice' : [ ?1, ?2 ] } }")
    fun findFollowingById(id: String, skip: Int, limit: Int): FollowingResponseList

    /**
     * Finds all the users that a specific user follows. (Taken from the "following" list)
     *
     * @param email: The email of the user
     */
    @Query(value="{ 'email' : ?0 }", fields="{ 'id' : 0, 'following' : 1 }")
    fun findAllFollowingByEmail(email: String): FollowingResponseList
}

interface BookDao: MongoRepository<Book, String> {

    /**
     * Returns the score for a specific book and the number of reviews.
     */
    @Query(value="{ 'id' : ?0 }", fields="{ 'reviews': 0 }")
    fun findScoreById(id: String): Optional<RatingResponse>
}