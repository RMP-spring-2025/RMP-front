package com.example.frontproject.data.model

data class AuthRequest(val username: String, val password: String)

data class AuthResponse(val accessToken: String, val refreshToken: String)

data class CreateUserProfileRequest(
    val username: String,
    val age: Int,
    val height: Double
)

data class CreateUserProfileResponse(
    val message: String
)