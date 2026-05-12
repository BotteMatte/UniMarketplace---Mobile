package com.example.unimarketplace.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_NAME = "user_name"
    }

    fun saveUserName(name: String?) {
        prefs.edit {
            putString(KEY_USER_NAME, name)
        }
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun clearSession() {
        prefs.edit {
            remove(KEY_USER_NAME)
        }
    }
}
