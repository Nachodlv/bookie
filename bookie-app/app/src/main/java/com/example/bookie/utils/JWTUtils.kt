package com.example.bookie.utils

import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT


object JWTUtils {

    @Throws(Exception::class)
    fun isJwtValid(JWTEncoded: String): Boolean {
        val jwt = getJwt(JWTEncoded)?: return false
        return !jwt.isExpired(0)
    }

    fun getJwt(JWTEncoded: String): JWT? {
        return try {
            JWT(JWTEncoded)
        } catch (e: DecodeException) { //Error
            e.printStackTrace()
            null
        }
    }

}