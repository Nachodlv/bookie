package com.bookie.backend.services

import com.bookie.backend.dto.*
import com.bookie.backend.models.Book
import com.bookie.backend.models.User
import org.springframework.data.domain.Pageable
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
    fun findFollowersById(id: String, skip: Int, limit: Int): Optional<FollowResponseList>

    /**
     * Finds the users that a specific user is following.
     *
     * @param id: The id of the user
     * @param skip: The amount of followers to skip before the first result (If using page and size, skip would be page * size)
     * @param limit: The maximum amount of followers to be returned
     */
    @Query(value="{ 'id' : ?0 }", fields="{ 'id' : 0, 'following' : { '\$slice' : [ ?1, ?2 ] } }")
    fun findFollowingById(id: String, skip: Int, limit: Int): Optional<FollowingResponseList>

    /**
     * Finds all the users that a specific user follows. (Taken from the "following" list)
     *
     * @param email: The email of the user
     */
    @Query(value="{ 'email' : ?0 }", fields="{ 'id' : 0, 'following' : 1 }")
    fun findAllFollowingByEmail(email: String): FollowingResponseList

    /**
     * Finds the reviews written by a specific user.
     *
     * @param id: The id of the user whose reviews will be returned
     * @param skip: The amount of reviews to skip before the first result (If using page and size, skip would be page * size)
     * @param limit: The maximum amount of reviews to be returned
     */
    @Query(value="{ 'id' : ?0 }", fields="{ 'id' : 0, 'reviews' : { '\$slice' : [ ?1, ?2 ] } }")
    fun findReviewsById(id: String, skip: Int, limit: Int): Optional<ReviewList>

    /**
     * Finds the user's whose firstName or lastName contain the query parameter
     */
    // Add pagination
    @Query(value="{ 'followers' : { '\$not' : {'\$elemMatch' : { 'id' : ?1 } } }, '\$or' : [ { 'firstName' : { '\$regex' : ?0, '\$options' : 'i' } }, { 'lastName' : { '\$regex' : ?0, '\$options' : 'i' } } ] }")
    fun findUsersByQueryParameter(q: String, id: String, pageable: Pageable): Optional<List<UserData>>
}

interface BookDao: MongoRepository<Book, String> {

    /**
     * Returns the score for a specific book and the number of reviews.
     */
    @Query(value="{ 'id' : ?0 }", fields="{ 'reviews': 0 }")
    fun findScoreById(id: String): Optional<RatingResponse>

    /**
     * Finds the reviews of a specific book.
     *
     * @param id: The id of the book whose reviews will be returned
     * @param skip: The amount of reviews to skip before the first result (If using page and size, skip would be page * size)
     * @param limit: The maximum amount of reviews to be returned
     */
    @Query(value="{ 'id' : ?0 }", fields="{ 'id' : 0, 'reviews' : { '\$slice' : [ ?1, ?2 ] } }")
    fun findReviewsById(id: String, skip: Int, limit: Int): Optional<ReviewList>
}