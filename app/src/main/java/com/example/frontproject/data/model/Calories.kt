package com.example.frontproject.data.model


data class CaloriesStatsResponse(
    val stats: List<CaloriesStat>
)

data class CaloriesStat(
    val time: String,
    val calories: Int
)