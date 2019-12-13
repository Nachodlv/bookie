package com.bookie.backend.controllers

import com.bookie.backend.dto.LoginRequest
import com.bookie.backend.dto.LoginResponse
import com.bookie.backend.security.CustomUserDetailsService
import com.bookie.backend.util.JwtTokenUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class AuthenticationController(private val authenticationManager: AuthenticationManager,
                               private val jwtTokenUtil: JwtTokenUtil,
                               private val userDetailsService: CustomUserDetailsService) {

    @PostMapping("/login")
    fun loginUser(@RequestBody login: LoginRequest): ResponseEntity<LoginResponse> {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(login.email, login.password))

        val userDetails: UserDetails = userDetailsService.loadUserByUsername(login.email)

        val token: String = jwtTokenUtil.generateToken(userDetails)

        return ResponseEntity(LoginResponse(token), HttpStatus.OK)
    }
}