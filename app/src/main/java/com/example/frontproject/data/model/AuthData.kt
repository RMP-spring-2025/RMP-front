package com.example.frontproject.data.model

import androidx.compose.ui.text.font.FontWeight

data class AuthRequest(val username: String, val password: String)

data class AuthResponse(val accessToken: String, val refreshToken: String)

data class CreateUserProfileRequest(
    val username: String,
    val age: Int,
    val height: Double,
    val weight: Double,
    val time: String,
    val goal: String
    )

data class CreateUserProfileResponse(
    val message: String
)