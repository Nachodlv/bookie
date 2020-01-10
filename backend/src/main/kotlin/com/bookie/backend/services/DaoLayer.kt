package com.bookie.backend.services

import com.bookie.backend.dto.FollowResponseList
import com.bookie.backend.models.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface UserDao: MongoRepository<User, String> {

    fun findByEmail(email: String): Optional<User>

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
     * Finds all the users that a specific user follows. (Taken from the "following" list)
     *
     * @param email: The email of the user
     */
    @Query(value="{ 'email' : ?0 }", fields="{ 'id' : 0, 'followers' : 1 }")
    fun findAllFollowingByEmail(email: String): FollowResponseList // We could return a FollowerList instead.
}