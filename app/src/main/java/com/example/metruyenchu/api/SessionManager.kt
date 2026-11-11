package com.example.metruyenchu.api

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "MTC_SESSION",
        Context.MODE_PRIVATE
    )

    fun saveLogin(response: LoginResponse) {
        prefs.edit()
            .putString("token", response.token)
            .putLong("userId", response.user.userId)
            .putString("userName", response.user.userName)
            .putString("email", response.user.email)
            .apply()
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    fun getUserId(): Long {
        return prefs.getLong("userId", -1L)
    }

    fun getUserName(): String? {
        return prefs.getString("userName", null)
    }

    fun getEmail(): String? {
        return prefs.getString("email", null)
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}