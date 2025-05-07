package com.example.frontproject.data.repository

import com.example.frontproject.api.ApiRequestExecutor
import com.example.frontproject.api.ApiService
import com.example.frontproject.domain.util.ResourceState

class CaloriesRepository(
    private val apiRequestExecutor: ApiRequestExecutor,
    private val apiService: ApiService
) {

    suspend fun getCaloriesForDate(from: String, to: String): ResourceState<Int> {
        return apiRequestExecutor.executeHeavyRequest(
            initialCall = { apiService.getCaloriesForRange(from, to) },
            pollingCall = { apiService.getCaloriesResponse(it) }
        ).let { result ->
            when (result) {
                is ResourceState.Success -> {
                    val totalCalories = result.data.stats.sumOf { it.calories }
                    if (totalCalories > 0) ResourceState.Success(totalCalories)
                    else ResourceState.Error("No calorie data")
                }
                is ResourceState.Error -> ResourceState.Error(result.message)
                ResourceState.Loading -> ResourceState.Loading
            }
        }
    }
}
