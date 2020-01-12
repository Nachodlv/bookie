package com.bookie.backend.services

import com.bookie.backend.models.User
import com.bookie.backend.security.CustomUserDetailsService
import com.bookie.backend.util.JwtTokenUtil
import com.bookie.backend.util.exceptions.UserNotFoundException
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*


@Service
class AuthenticationService(private val userService: UserService,
                            private val mailSender: MailSender,
                            private val userDetailsService: CustomUserDetailsService,
                            private val jwtTokenUtil: JwtTokenUtil) {

    fun attemptPasswordReset(email: String) {
        val user: Optional<User> = userService.getByEmail(email)
        if (user.isPresent) {
            val token = buildUserToken(email)
            val message = buildEmail(email, token)
            mailSender.send(message)
        } else {
            throw UserNotFoundException("No user with that id was found.")
        }
    }

    private fun buildUserToken(email: String): String {
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(email)

        return jwtTokenUtil.generateToken(userDetails)
    }

    private fun buildEmail(email: String, token: String): SimpleMailMessage {
        val message = SimpleMailMessage()
        message.setTo(email)
        message.setSubject("Bookie Account Password Reset")
        message.setText(
                "Reset your password by clicking on the following link: bookie.app://reset-password?token=$token"
        )
        return message
    }
}