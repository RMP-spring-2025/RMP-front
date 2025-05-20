package com.example.frontproject.data.repository

import com.example.frontproject.api.ApiRequestExecutor
import com.example.frontproject.api.ApiService
import com.example.frontproject.domain.util.ResourceState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CaloriesRepository(
    private val apiRequestExecutor: ApiRequestExecutor,
    private val apiService: ApiService
) {

    // Находит сумму всех калорий за промежуток
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

    // Возвращает массив все каллорий с timestamp-ом
    suspend fun getCaloriesArray(from: String, to: String): ResourceState<List<Pair<Long, Int>>> {
        return apiRequestExecutor.executeHeavyRequest(
            initialCall = { apiService.getCaloriesForRange(from, to) },
            pollingCall = { apiService.getCaloriesResponse(it) }
        ).let { result ->
            when (result) {
                is ResourceState.Success -> {
                    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

                    // Группируем по дате (LocalDate)
                    val grouped = result.data.stats
                        .groupBy { stat ->
                            LocalDate.parse(stat.time, formatter)
                        }
                        .mapValues { (_, stats) ->
                            stats.sumOf { it.calories }
                        }

                    // Сформировать полный список за период
                    val fullList = mutableListOf<Pair<Long, Int>>()
                    val fromDate = LocalDate.parse(from.substring(0, 10))
                    val toDate = LocalDate.parse(to.substring(0, 10))

                    var currentDate = fromDate
                    while (!currentDate.isAfter(toDate)) {
                        val timestamp = currentDate
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
                        val calories = grouped[currentDate] ?: 0
                        fullList.add(timestamp to calories)
                        currentDate = currentDate.plusDays(1)
                    }

                    ResourceState.Success(fullList)
                }
                is ResourceState.Error -> ResourceState.Error(result.message)
                ResourceState.Loading -> ResourceState.Loading
            }
        }
    }
}
