package com.bookie.backend.controllers

import com.bookie.backend.dto.FollowRequest
import com.bookie.backend.dto.FollowResponse
import com.bookie.backend.dto.FollowResponseList
import com.bookie.backend.models.Follower
import com.bookie.backend.models.User
import com.bookie.backend.services.UserService
import com.bookie.backend.util.JwtTokenUtil
import com.bookie.backend.util.exceptions.FollowingException
import com.bookie.backend.util.exceptions.SelfFollowingException
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
            } catch (e3: SelfFollowingException) {
                ResponseEntity(HttpStatus.BAD_REQUEST)
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

    /**
     * Returns the followers of a specific user (identified by the provided id)
     *
     * The result provides information on whether the currently logged user is following each user in it or not.
     *
     * The data returned has the following structure:
     *
     * [
     *     {
     *         id: String
     *         firstName: String
     *         lastName: String
     *         followed: Boolean
     *     }
     * ]
     *
     * The followed attribute will be true if the current user is following that user.
     */
    @GetMapping("/followers/{id}")
    fun getFollowers(@PathVariable id: String,
                     @RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
                     @RequestParam(value = "size",required = false, defaultValue = "10") size: Int,
                     @RequestHeader headers: Map<String, String>): ResponseEntity<List<FollowResponse>> {
        val token = headers["authorization"]?.substring(7)
        if (token !== null) {
            return try {
                ResponseEntity(userService.getFollowers(id, page, size, token), HttpStatus.OK)
            } catch (e: UserNotFoundException) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }

        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    /**
     * Returns the users that a specific user follows (identified by the provided id)
     *
     * The result provides information on whether the currently logged user is following each user in it or not.
     *
     * The data returned has the following structure:
     *
     * [
     *     {
     *         id: String
     *         firstName: String
     *         lastName: String
     *         followed: Boolean
     *     }
     * ]
     *
     * The followed attribute will be true if the current user is following that user.
     */
    @GetMapping("/following/{id}")
    fun getFollowing(@PathVariable id: String,
                     @RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
                     @RequestParam(value = "size",required = false, defaultValue = "10") size: Int,
                     @RequestHeader headers: Map<String, String>): ResponseEntity<List<FollowResponse>> {
        val token = headers["authorization"]?.substring(7)
        if (token !== null) {
            return try {
                ResponseEntity(userService.getFollowing(id, page, size, token), HttpStatus.OK)
            } catch (e: UserNotFoundException) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }

        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }

    /**
     * Checks if the currently logged in user follows a specific user.
     */
    @GetMapping("/checkFollowing/{id}")
    fun checkFollowing(@PathVariable id: String,
                       @RequestHeader headers: Map<String, String>) : ResponseEntity<Boolean> {
        val token = headers["authorization"]?.substring(7)
        if (token !== null) {
            return ResponseEntity(userService.checkSpecificFollowing(token, id), HttpStatus.OK)
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }
}