package com.example.bookie.dao

import android.content.Context
import com.auth0.android.jwt.JWT
import com.example.bookie.R
import com.example.bookie.utils.JWTUtils
import org.json.JSONObject

class SharedPreferencesDao(val context: Context?) {

    fun saveToken(token: String) {
        if (context == null) return
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        ) ?: return
        with(sharedPref.edit()) {
            putString(context.getString(R.string.login_token), JSONObject(token)["token"] as String)
            commit()
        }

    }

    fun getToken(): String? {
        val sharedPref = context?.getSharedPreferences(
            context.getString(R.string.shared_preferences), Context.MODE_PRIVATE
        )
        return sharedPref?.getString(context?.getString(R.string.login_token), null)
    }

    fun getTokenAsJwt(): JWT? {
        val token = getToken()?: return null
        return JWTUtils.getJwt(token)
    }

    fun isTokenValid(): Boolean {
        val token = getToken()?: return false
        return JWTUtils.isJwtValid(token)
    }
}