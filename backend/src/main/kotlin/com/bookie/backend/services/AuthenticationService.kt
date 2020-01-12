package com.bookie.backend.services

import com.bookie.backend.models.User
import com.bookie.backend.util.exceptions.UserNotFoundException
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Service
import java.util.*


@Service
class AuthenticationService(private val userService: UserService,
                            private val mailSender: MailSender) {

    fun attemptPasswordReset(email: String) {
        // Send the email
        val user: Optional<User> = userService.getByEmail(email)
        if (user.isPresent) {
            val message = SimpleMailMessage()
            message.setTo(email)
            message.setSubject("Bookie Account Password Change")
            message.setText("Change your password by clicking on the following link:") // How do we create the link?
            mailSender.send(message) // Try this out.
        } else {
            throw UserNotFoundException("No user with that id was found.")
        }
    }
}