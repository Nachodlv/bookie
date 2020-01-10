package com.bookie.backend.controllers

import com.bookie.backend.dto.FollowRequest
import com.bookie.backend.dto.FollowResponse
import com.bookie.backend.dto.FollowResponseList
import com.bookie.backend.models.Follower
import com.bookie.backend.models.User
import com.bookie.backend.services.UserService
import com.bookie.backend.util.JwtTokenUtil
import com.bookie.backend.util.exceptions.FollowingException
import com.bookie.backend.util.exceptions.UserNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.websocket.server.PathParam

@RestController
@RequestMapping("/user")
class FollowController(private val userService: UserService) {

    /**
     * Follows a user.
     *
     * Two users are involved in this operation, the follower and the followed user.
     *
     * The data required for the user is the following:
     *
     * {
     *     userId: String
     * }
     *
     * The userId belongs to the followed user, which the currently logged in user wants to follow.
     *
     * @return {@code HttpStatus.OK} the follow was successful
     * @return {@code HttpStatus.CONFLICT} the currently logged user was already following the specified user
     * @return {@code HttpStatus.NOT_FOUND} the provided id for the followed user does not exist
     */
    @PostMapping("/follow")
    fun follow(@RequestBody followRequest: FollowRequest,
               @RequestHeader headers: Map<String, String>): ResponseEntity<FollowResponse> {
        val token = headers["authorization"]?.substring(7)

        if (token != null) {
            return try {
                val followedUser = this.userService.followUser(token, followRequest.userId)
                ResponseEntity(followedUser.id?.let { FollowResponse(it, followedUser.firstName, followedUser.lastName, true) }, HttpStatus.OK)
            } catch (e1: FollowingException) {
                ResponseEntity(HttpStatus.CONFLICT)
            } catch (e2: UserNotFoundException) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED) // This shouldn't happen.
    }

    /**
     * Unfollows a user.
     *
     * The data required for the user is the following:
     *
     * {
     *     userId: String
     * }
     *
     * The userId belongs to the unfollowed user, which the currently logged in user wants to unfollow.
     *
     * @return {@code HttpStatus.OK} the follow was successful
     * @return {@code HttpStatus.CONFLICT} the currently logged user was already following the specified user
     * @return {@code HttpStatus.NOT_FOUND} the provided id for the followed user does not exist
     */
    @PostMapping("/unfollow")
    fun unfollow(@RequestBody followRequest: FollowRequest,
               @RequestHeader headers: Map<String, String>): ResponseEntity<FollowResponse> {
        val token = headers["authorization"]?.substring(7)

        if (token !== null) {
            return try {
                val unfollowedUser = this.userService.unfollowUser(token, followRequest.userId)
                ResponseEntity(unfollowedUser.id?.let { FollowResponse(it, unfollowedUser.firstName, unfollowedUser.lastName, false) }, HttpStatus.OK)
            } catch (e1: FollowingException) {
                return ResponseEntity(HttpStatus.CONFLICT) // We could return some other exception.
            } catch (e2: UserNotFoundException) {
                return ResponseEntity(HttpStatus.NOT_FOUND)
            }
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    // Add pagination
    /**
     * Returns the followers of a specific user (identified by the provided id)
     *
     * The result provides information on whether the currently logged user is following each user in it or not.
     */
    @GetMapping("/followers/{id}")
    fun getFollowers(@PathVariable id: String,
                     @PathParam(value = "page") page: Int = 0,
                     @PathParam(value = "size") size: Int = 10): List<FollowResponse> { // ResponseEntity<Page<FollowResponse>> Page<List<Follower>>
        return userService.getFollowers(id, page, size)
    }

    // Add pagination
    /**
     * Returns the users that a specific user follows (identified by the provided id)
     *
     * The result provides information on whether the currently logged user is following each user in it or not.
     */
    @GetMapping("/following/{id}")
    fun getFollowing(@PathVariable id: String, pageable: Pageable) {
        // Same DTO as the one used in getFollowers.
        this.userService.getFollowing(id, pageable)
    }
}