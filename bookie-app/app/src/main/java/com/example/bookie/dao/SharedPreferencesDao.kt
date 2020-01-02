package com.example.bookie.dao

import android.content.Context
import com.example.bookie.R
import com.example.bookie.utils.JWTUtils

class SharedPreferencesDao(val context: Context?) {

    fun saveToken(token: String) {
        if (context == null) return
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        ) ?: return
        with(sharedPref.edit()) {
            putString(context.getString(R.string.login_token), token)
            commit()
        }

    }

    fun getToken(): String? {
        val sharedPref = context?.getSharedPreferences(
            context.getString(R.string.shared_preferences), Context.MODE_PRIVATE
        )
        val tokenEncoded: String? =
            sharedPref?.getString(context?.getString(R.string.login_token), null)

        return if (tokenEncoded != null) JWTUtils.decoded(tokenEncoded) else null
    }
}