package com.bookie.backend.security

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder

@EnableWebSecurity
class WebSecurityConfiguration(
        private val passwordEncoderAndMatcher: PasswordEncoder,
        private val customUserDetailsService: CustomUserDetailsService): WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoderAndMatcher)
    }
}