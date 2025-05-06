package com.example.frontproject.api

import com.example.frontproject.store.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request // Убедитесь, что Request импортирован
import okhttp3.Response

class AuthInterceptor(private val tokenRepository: TokenRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { tokenRepository.getCurrentAccessToken() }

        val requestBuilder: Request.Builder = originalRequest.newBuilder()
        token?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        return response
    }
}