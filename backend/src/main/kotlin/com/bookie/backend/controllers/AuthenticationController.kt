package com.bookie.backend.controllers

import com.bookie.backend.dto.LoginRequest
import com.bookie.backend.dto.LoginResponse
import com.bookie.backend.dto.PasswordChangeRequest
import com.bookie.backend.dto.PasswordResetRequest
import com.bookie.backend.security.CustomUserDetailsService
import com.bookie.backend.services.AuthenticationService
import com.bookie.backend.services.UserService
import com.bookie.backend.util.JwtTokenUtil
import com.bookie.backend.util.exceptions.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.*
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*


@RestController
class AuthenticationController(private val authenticationManager: AuthenticationManager,
                               private val jwtTokenUtil: JwtTokenUtil,
                               private val userDetailsService: CustomUserDetailsService,
                               private val authenticationService: AuthenticationService,
                               private val userService: UserService) {

    /**
     * Logs the user in, returning a token which can later be used to make calls to secure routes.
     *
     * In subsequent calls, the token much be included in the Authorization header.
     * The value of the authorization header must be 'Bearer {token}',
     * where {token} is the token returned by this endpoint.
     *
     * The credentials must have the following content:
     *
     * {
     *      email: String,
     *      password: String
     * }
     *
     * This route requires no authentication
     *
     * @return {@code HttpStatus.OK} and the token if the login was successful
     * @return {@code HttpStatus.UNAUTHORIZED} if the login credentials are invalid
     */
    @PostMapping("/login")
    fun loginUser(@RequestBody login: LoginRequest): ResponseEntity<LoginResponse> {

        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(login.email, login.password))
        } catch (e: DisabledException) {
            throw Exception("USER_DISABLED", e)
        } catch (e: LockedException) {
            throw Exception("USER_LOCKED", e)
        } catch (e: BadCredentialsException) {
            throw Exception("INVALID_CREDENTIALS", e)
        }

        val userDetails: UserDetails = userDetailsService.loadUserByUsername(login.email)

        val token: String = jwtTokenUtil.generateToken(userDetails)

        // Should we add the token in the response header?
        // If we return the token in the body, it still will need to be in the header for the request to be authorized.

        return ResponseEntity(LoginResponse(token), HttpStatus.OK)
    }

    /**
     * Allows a user to reset their password.
     *
     * Sends an email to the provided email (in case an account with that email exists).
     * With this email, the user is able to choose a new password for his account.
     *
     * The request should have the following structure:
     *
     * {
     *     email: String
     * }
     *
     * This route requires no authentication
     */
    @PutMapping("/requestPasswordReset")
    fun requestPasswordChange(@RequestBody request: PasswordResetRequest): ResponseEntity<String> { // What should we send here?
        return try {
            authenticationService.attemptPasswordReset(request.email)
            ResponseEntity("Password change email sent", HttpStatus.OK)
        } catch (e: UserNotFoundException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Resets a user password, setting it to the one passed as parameter.
     *
     * The request should have the following structure:
     *
     * {
     *     newPassword: String
     * }
     *
     * This route requires authentication.
     * In order to be authenticated, the authentication token is sent in the password reset email.
     * No login is required to use this method, as the user can not log in before resetting his password.
     */
    @PutMapping("/resetPassword")
    fun changePassword(@RequestBody request: PasswordChangeRequest,
                       @RequestHeader headers: Map<String, String>): ResponseEntity<String> {
        val token = headers["authorization"]?.substring(7)

        if (token != null) {
            return try {
                userService.resetUserPassword(token, request.newPassword)
                ResponseEntity("Password changed successfully", HttpStatus.OK)
            } catch (e: UserNotFoundException) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            }
        }
        return ResponseEntity(HttpStatus.UNAUTHORIZED)
    }
}