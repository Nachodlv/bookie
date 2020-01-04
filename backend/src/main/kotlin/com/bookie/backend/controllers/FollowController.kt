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
@RequestMapping("/follow")
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
    @PostMapping
    fun follow(@RequestBody followRequest: FollowRequest,
               @RequestHeader headers: Map<String, String>): ResponseEntity<FollowResponse> { // We shouldn't return a user here.
        // Find the user that made the request. Find out who it is and its data in order to update.

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

        /*
        val email = tokenUtil.getUsernameFromToken(token)

        val loggedUser = userService.getByEmail(email)

        // Find out if the user provided in the request actually exists (If is doesn't, return some type of error.

        val followId = followRequest.userId

        val followUser = userService.getById(followId) // Return some error if the user is not found, how should it be handled?

        // This should be done by the service, I'll leave it here for now just to see if it works.
        try {
            followUser.ifPresent{ user -> user.addFollower(loggedUser.get())} // This should be handled by the service
        } catch (e: FollowingException) {
            // Return a message saying that the user was already following the other user.
        }

        userService.update(loggedUser.get())

        userService.update(followUser.get())

        // Update both users.

        // Return

         */
    }
}