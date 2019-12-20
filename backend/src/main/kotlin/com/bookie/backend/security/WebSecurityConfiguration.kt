package com.bookie.backend.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(
        private val passwordEncoderAndMatcher: PasswordEncoder,
        private val customUserDetailsService: CustomUserDetailsService,
        private val jwtRequestFilter: JwtRequestFilter,
        private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint): WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/user/register")
                .permitAll()
                .antMatchers("/login")
                .permitAll()
                .anyRequest().authenticated()
                /*
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                 */
                .and()
                .logout()
                .permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)

        // What is happening here?
        // http.addFilterBefore(UsernamePasswordAuthenticationFilter(), jwtRequestFilter::class.java)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoderAndMatcher)
    }

    @Bean
    @Throws(java.lang.Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }
}