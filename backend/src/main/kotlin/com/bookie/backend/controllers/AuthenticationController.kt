package com.bookie.backend.controllers

import com.bookie.backend.dto.LoginRequest
import com.bookie.backend.dto.LoginResponse
import com.bookie.backend.security.CustomUserDetailsService
import com.bookie.backend.util.JwtTokenUtil
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.*
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class AuthenticationController(private val authenticationManager: AuthenticationManager,
                               private val jwtTokenUtil: JwtTokenUtil,
                               private val userDetailsService: CustomUserDetailsService) {

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
}