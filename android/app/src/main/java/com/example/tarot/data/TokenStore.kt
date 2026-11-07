package com.example.tarot.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenStore(context: Context) {

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var token: String?
        get() = preferences.getString(KEY_TOKEN, null)
        set(value) {
            preferences.edit {
                if (value.isNullOrBlank()) {
                    remove(KEY_TOKEN)
                } else {
                    putString(KEY_TOKEN, value)
                }
            }
        }

    val isLoggedIn: Boolean
        get() = !token.isNullOrBlank()

    fun clear() {
        preferences.edit { remove(KEY_TOKEN) }
    }

    private companion object {
        const val PREFS_NAME = "mystic_tarot_auth"
        const val KEY_TOKEN = "access_token"
    }
}
