package com.bookie.backend.services

import com.bookie.backend.models.User
import com.bookie.backend.security.CustomUserDetailsService
import com.bookie.backend.util.JwtTokenUtil
import com.bookie.backend.util.exceptions.UserNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.mail.javamail.JavaMailSender

@Service
class AuthenticationService(private val userService: UserService,
                            private val mailSender: JavaMailSender,
                            private val userDetailsService: CustomUserDetailsService,
                            private val jwtTokenUtil: JwtTokenUtil) {

    fun attemptPasswordReset(email: String) {
        val user: Optional<User> = userService.getByEmail(email)
        if (user.isPresent) {
            val token = buildUserToken(email)
            val message = buildEmailContent(token)
            val mimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage)
            helper.setText(message, true)
            helper.setSubject("Bookie Account Password Reset")
            helper.setTo(email)
            mailSender.send(mimeMessage)
        } else {
            throw UserNotFoundException("No user with that id was found.")
        }
    }

    private fun buildUserToken(email: String): String {
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(email)

        return jwtTokenUtil.generateToken(userDetails)
    }

    private fun buildEmailContent(token: String): String {
        return "<html>Reset your password by clicking on the following link: <a href=\"bookie.app://reset-password?token=$token\">Click Me</a></html>"
    }
}