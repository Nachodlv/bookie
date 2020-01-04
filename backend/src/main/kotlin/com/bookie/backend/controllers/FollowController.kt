package com.bookie.backend.controllers

import com.bookie.backend.dto.FollowRequest
import com.bookie.backend.dto.FollowResponse
import com.bookie.backend.services.UserService
import com.bookie.backend.util.JwtTokenUtil
import com.bookie.backend.util.exceptions.FollowingException
import com.bookie.backend.util.exceptions.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

        try {
            if (token != null) {
                this.userService.followUser(token, followRequest.userId)
            }
        } catch (e1: FollowingException) {
            return ResponseEntity(HttpStatus.CONFLICT)
        } catch (e2: UserNotFoundException) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        return ResponseEntity(FollowResponse("User followed successfully"), HttpStatus.OK)
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

        try {
            if (token != null) {
                this.userService.unfollowUser(token, followRequest.userId)
            }
        } catch (e1: FollowingException) {
            return ResponseEntity(HttpStatus.CONFLICT) // We could return some other exception.
        } catch (e2: UserNotFoundException) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }

        return ResponseEntity(FollowResponse("User unfollowed successfully"), HttpStatus.OK)
    }
}