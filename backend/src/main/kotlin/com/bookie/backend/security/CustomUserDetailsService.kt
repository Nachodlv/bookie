package com.bookie.backend.security

import com.bookie.backend.models.User
import com.bookie.backend.services.UserService
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
@Transactional
class CustomUserDetailsService(private val userService: UserService) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user: User = userService.getByEmail(email).orElseThrow {
            throw UsernameNotFoundException("No user found with email:$email")
        }

        return org.springframework.security.core.userdetails.User(
                user.email,
                user.password,
                true,
                true,
                true,
                true,
                getAuthorities(user.roles))
    }

    fun getAuthorities(roles: List<String>): Collection<GrantedAuthority> {
        return roles
                .stream()
                .map { role ->
                    SimpleGrantedAuthority(role.toString())
                }
                .collect(Collectors.toList())
    }
}