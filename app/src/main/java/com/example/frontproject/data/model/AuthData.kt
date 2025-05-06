package com.example.frontproject.data.model

data class AuthRequest(val username: String, val password: String)

data class AuthResponse(val accessToken: String, val refreshToken: String)