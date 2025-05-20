package com.example.frontproject.data.model.stats

data class UserProfileStatsResponse(
    val goal: String,
    val weight: Double,
    val userId: String,
    val age: Int,
    val username: String,
    val height: Double,
    val sex: String,
)