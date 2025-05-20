package com.example.frontproject

import android.content.Context
import com.example.frontproject.api.ApiRequestExecutor
import com.example.frontproject.api.ApiService
import com.example.frontproject.api.AuthInterceptor
import com.example.frontproject.api.TokenRefreshAuthenticator
import com.example.frontproject.data.repository.CaloriesRepository
import com.example.frontproject.data.repository.HealthConnectRepositoryImpl
import com.example.frontproject.data.repository.HealthConnectRepository
import com.example.frontproject.data.repository.MealsRepository
import com.example.frontproject.data.repository.UserProfileRepository
import com.example.frontproject.store.SettingsRepository
import com.example.frontproject.store.TokenRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

    val tokenRepository: TokenRepository by lazy {
        TokenRepository(context.applicationContext)
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(context.applicationContext)
    }

    private val currentBaseUrl: String by lazy {
        settingsRepository.getBaseUrl() ?: SettingsRepository.DEFAULT_BASE_URL
    }

    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .create()
    }

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    private val authInterceptor: AuthInterceptor by lazy {
        AuthInterceptor(tokenRepository)
    }

    private val tokenRefreshAuthenticator: TokenRefreshAuthenticator by lazy {
        TokenRefreshAuthenticator(tokenRepository, retrofitBuilder)
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenRefreshAuthenticator)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        retrofitBuilder
            .client(okHttpClient)
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val apiRequestExecutor: ApiRequestExecutor by lazy {
        ApiRequestExecutor(apiService)
    }

    val mealsRepository: MealsRepository by lazy {
        MealsRepository(apiRequestExecutor, apiService)
    }

    val caloriesRepository: CaloriesRepository by lazy {
        CaloriesRepository(apiRequestExecutor, apiService)
    }

    val userProfileRepository: UserProfileRepository by lazy {
        UserProfileRepository(apiRequestExecutor)
    }

    val healthConnectRepository: HealthConnectRepository by lazy {
        HealthConnectRepositoryImpl(context.applicationContext)
    }
}