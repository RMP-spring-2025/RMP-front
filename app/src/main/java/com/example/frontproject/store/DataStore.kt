package com.example.frontproject.store

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import android.util.Log

class TokenRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val TAG = "TokenRepository"
    }

    // Сохранение обоих токенов (например, после логина)
    fun saveTokens(accessToken: String, refreshToken: String) {
        Log.d(TAG, "Saving both tokens: AT: $accessToken, RT: $refreshToken")
        prefs.edit {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
        }
    }

    // Сохранение только access token (например, после обновления токена)
    fun saveAccessToken(accessToken: String) {
        Log.d(TAG, "Saving Access Token: $accessToken")
        prefs.edit {
            putString(ACCESS_TOKEN_KEY, accessToken)
        }
    }

    fun clearTokens() {
        Log.d(TAG, "Clearing tokens")
        prefs.edit {
            remove(ACCESS_TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
        }
    }

    fun getCurrentAccessToken(): String? {
        val token = prefs.getString(ACCESS_TOKEN_KEY, null)
        Log.d(TAG, "Getting Access Token: $token")
        return token
    }

    fun getCurrentRefreshToken(): String? {
//        val token =
//            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ3aW54LWNsdWItYXVkaWVuY2UiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0LyIsInVzZXJuYW1lIjoic3RyaW5nIiwiZXhwIjoxNzQ3NzYwODEyfQ.Of9Sr4Jrh94rYUoELYBDcunXrFx5WgTlC2898Rdl348"
         val token = prefs.getString(REFRESH_TOKEN_KEY, null)
        Log.d(TAG, "Getting Refresh Token: $token")
        return token
    }
}

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "settings_prefs"
        private const val BASE_URL_KEY = "base_url"
        private const val TAG = "SettingsRepository"
        const val DEFAULT_BASE_URL = "http://192.168.31.111:8080" // Ваш URL по умолчанию
    }

    fun saveBaseUrl(baseUrl: String) {
        Log.d(TAG, "Saving Base URL: $baseUrl")
        prefs.edit {
            putString(BASE_URL_KEY, baseUrl)
        }
    }

    fun getBaseUrl(): String? {
        val url = prefs.getString(BASE_URL_KEY, null)
        Log.d(TAG, "Getting Base URL: $url")
        return url
    }
}

