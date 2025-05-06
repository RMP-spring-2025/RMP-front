package com.example.frontproject.api

import android.util.Log
import com.example.frontproject.store.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.*
import retrofit2.Retrofit

class TokenRefreshAuthenticator(
    private val tokenRepository: TokenRepository,
    private val retrofitBuilder: Retrofit.Builder
) : Authenticator {

    companion object {
        private const val TAG = "TokenAuthenticator"
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d(TAG, "Authenticate triggered for response: ${response.code} on ${response.request.url}")
        Log.d(TAG, "Response count: ${response.responseCount}")

        val currentRefreshToken = runBlocking { tokenRepository.getCurrentRefreshToken() }
        Log.d(TAG, "Current Refresh Token: $currentRefreshToken")

        if (currentRefreshToken == null || response.responseCount >= 2) {
            Log.w(TAG, "Cannot refresh token. Refresh token is null or too many retries.")
            return null
        }

        Log.d(TAG, "Attempting to refresh token...")
        return runBlocking {
            try {
                val tempApiService = retrofitBuilder
                    .client(OkHttpClient.Builder().build())
                    .build()
                    .create(ApiService::class.java)

                Log.d(TAG, "Calling refresh token API with RT: $currentRefreshToken")
                val refreshResponse = tempApiService.refreshToken(RefreshTokenRequest(currentRefreshToken))
                Log.d(TAG, "Refresh token API response: Code=${refreshResponse.code()}, Success=${refreshResponse.isSuccessful}, Body=${refreshResponse.body()}")

                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val newAccessTokenResponse = refreshResponse.body()!!
                    Log.i(TAG, "Token refreshed successfully. New AT: ${newAccessTokenResponse.token}")
                    tokenRepository.saveAccessToken(newAccessTokenResponse.token)

                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${newAccessTokenResponse.token}")
                        .build()
                } else {
                    Log.e(TAG, "Failed to refresh token. Response code: ${refreshResponse.code()}, Error body: ${refreshResponse.errorBody()?.string()}")
                    // Если рефреш не удался (например, refresh token невалиден), возможно, стоит очистить токены
                    // и перенаправить пользователя на логин.
                    // runBlocking { tokenRepository.clearTokens() }
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during token refresh", e)
                // runBlocking { tokenRepository.clearTokens() }
                null
            }
        }
    }

    private val Response.responseCount: Int
        get() {
            var count = 0
            var prior = priorResponse
            while (prior != null) {
                count++
                prior = prior.priorResponse
            }
            return count
        }
}