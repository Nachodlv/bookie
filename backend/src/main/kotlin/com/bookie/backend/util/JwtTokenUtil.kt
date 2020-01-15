package com.bookie.backend.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import javax.crypto.spec.SecretKeySpec

@Component
class JwtTokenUtil : Serializable {
    @Value("\${jwt.secret}")
    private val secret: String? = null

    /**
     * Retrieves the username from the token.
     */
    fun getUsernameFromToken(token: String?): String {
        return getClaimFromToken(token).subject
    }

    /**
     * Retrieves the expiration date from the token.
     */
    fun getExpirationDateFromToken(token: String?): Date {
        return getClaimFromToken(token).expiration
    }

    fun getClaimFromToken(token: String?): Claims {
        return getAllClaimsFromToken(token)
    }

    /**
     * Retrieves all claims from the token by using the same key that was used in its creation.
     */
    private fun getAllClaimsFromToken(token: String?): Claims {
        return Jwts
                .parser()
                .setSigningKey(SecretKeySpec(secret?.toByteArray(), "HmacSHA256"))
                .parseClaimsJws(token)
                .body
    }

    /**
     * Checks if the token has expired.
     */
    private fun isTokenExpired(token: String?): Boolean {
        val expiration: Date = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    /**
     * Generates a new token for the specified user.
     */
    fun generateToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = HashMap()
        return doGenerateToken(claims, userDetails.username)
    }

    /**
     * Generates the JWT Token
     *
     * 1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
     * 2. Sign the JWT using the HS512 algorithm and secret key.
     * 3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
     *    compaction of the JWT to a URL-safe string
     */
    private fun doGenerateToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SecretKeySpec(secret?.toByteArray(), "HmacSHA256"))
                .compact()
    }

    /**
     * Validates the token by checking that it belongs to the user and it hasn't expired.
     */
    fun validateToken(token: String?, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    companion object {
        private const val serialVersionUID = -2550185165626007488L
        const val JWT_TOKEN_VALIDITY = 5 * 60 * 60.toLong()
    }
}