package com.example.bookie.utils

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


object JWTUtils {

    @Throws(Exception::class)
    fun decoded(JWTEncoded: String): String? {
        return try {
            val split = JWTEncoded.split("\\.").toTypedArray()
            print("Header: " + getJson(split[0]))
            print("Body: " + getJson(split[1]))
            split[1]
        } catch (e: UnsupportedEncodingException) { //Error
            e.printStackTrace()
            null
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getJson(strEncoded: String): String {
        val decodedBytes: ByteArray = Base64.decode(strEncoded, Base64.URL_SAFE)
        return String(decodedBytes, Charset.forName("UTF-8"))
    }
}