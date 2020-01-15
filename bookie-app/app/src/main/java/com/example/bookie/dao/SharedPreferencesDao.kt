package com.example.bookie.dao

import android.content.Context
import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.example.bookie.R
import com.example.bookie.utils.JWTUtils
import org.json.JSONObject

class SharedPreferencesDao(val context: Context?) {

    private val loginTokenKey: String? = context?.getString(R.string.login_token)

    fun saveToken(token: String) {
        val sharedPref = getSharedPreferences()?: return
        with(sharedPref.edit()) {
            putString(loginTokenKey, JSONObject(token)["token"] as String)
            commit()
        }

    }

    fun deleteToken() {
        val sharedPref = getSharedPreferences()?: return
        with(sharedPref.edit()) {
            remove(loginTokenKey)
            commit()
        }
    }

    fun getToken(): String? =
        getSharedPreferences()?.getString(context?.getString(R.string.login_token), null)

    fun getTokenAsJwt(): JWT? {
        val token = getToken() ?: return null
        return JWTUtils.getJwt(token)
    }

    fun isTokenValid(): Boolean {
        val token = getToken() ?: return false
        return JWTUtils.isJwtValid(token)
    }

    private fun getSharedPreferences(): SharedPreferences? =
        context?.getSharedPreferences(
            context.getString(R.string.shared_preferences), Context.MODE_PRIVATE
        )

    companion object {
        fun getToken(context: Context): String? {
            return context.getSharedPreferences(
                context.getString(R.string.shared_preferences), Context.MODE_PRIVATE
            )?.getString(context.getString(R.string.login_token), null)
        }
    }
}