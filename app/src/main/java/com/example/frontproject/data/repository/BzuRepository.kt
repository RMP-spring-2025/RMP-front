package com.example.frontproject.data.repository

import com.example.frontproject.api.ApiRequestExecutor
import com.example.frontproject.api.ApiService
import com.example.frontproject.data.model.BzuStat
import com.example.frontproject.domain.util.ResourceState
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.ZoneId

class BzuRepository(
    private val apiRequestExecutor: ApiRequestExecutor,
    private val apiService: ApiService
) {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    suspend fun getProteinsForDate(from: String, to: String): ResourceState<Int> {
        return apiRequestExecutor.executeHeavyRequest(
            initialCall = { apiService.getBzuForRange(from, to) },
            pollingCall = { apiService.getBzuResponse(it) }
        ).let { result ->
            when (result) {
                is ResourceState.Success -> {
                    val totalProteins = result.data.stats.sumOf { it.b }
                    if (totalProteins > 0) ResourceState.Success(totalProteins)
                    else ResourceState.Error("No protein data")
                }
                is ResourceState.Error -> ResourceState.Error(result.message)
                ResourceState.Loading -> ResourceState.Loading
            }
        }
    }

    suspend fun getFatsForDate(from: String, to: String): ResourceState<Int> {
        return apiRequestExecutor.executeHeavyRequest(
            initialCall = { apiService.getBzuForRange(from, to) },
            pollingCall = { apiService.getBzuResponse(it) }
        ).let { result ->
            when (result) {
                is ResourceState.Success -> {
                    val totalProteins = result.data.stats.sumOf { it.z }
                    if (totalProteins > 0) ResourceState.Success(totalProteins)
                    else ResourceState.Error("No fat data")
                }
                is ResourceState.Error -> ResourceState.Error(result.message)
                ResourceState.Loading -> ResourceState.Loading
            }
        }
    }

    suspend fun getCarbsForDate(from: String, to: String): ResourceState<Int> {
        return apiRequestExecutor.executeHeavyRequest(
            initialCall = { apiService.getBzuForRange(from, to) },
            pollingCall = { apiService.getBzuResponse(it) }
        ).let { result ->
            when (result) {
                is ResourceState.Success -> {
                    val totalProteins = result.data.stats.sumOf { it.u }
                    if (totalProteins > 0) ResourceState.Success(totalProteins)
                    else ResourceState.Error("No carbs data")
                }
                is ResourceState.Error -> ResourceState.Error(result.message)
                ResourceState.Loading -> ResourceState.Loading
            }
        }
    }

    suspend fun getProteinArray(from: String, to: String): ResourceState<List<Pair<Long, Int>>> {
        return getBzuArrayByType(from, to) { stat -> stat.b }
    }

    suspend fun getFatArray(from: String, to: String): ResourceState<List<Pair<Long, Int>>> {
        return getBzuArrayByType(from, to) { stat -> stat.z }
    }

    suspend fun getCarbsArray(from: String, to: String): ResourceState<List<Pair<Long, Int>>> {
        return getBzuArrayByType(from, to) { stat -> stat.u }
    }

    private suspend fun getBzuArrayByType(
        from: String,
        to: String,
        valueSelector: (BzuStat) -> Int
    ): ResourceState<List<Pair<Long, Int>>> {
        return apiRequestExecutor.executeHeavyRequest(
            initialCall = { apiService.getBzuForRange(from, to) },
            pollingCall = { apiService.getBzuResponse(it) }
        ).let { result ->
            when (result) {
                is ResourceState.Success -> {
                    // Группируем stats по дате (LocalDate) и суммируем выбранное поле
                    val grouped = result.data.stats
                        .groupBy { stat -> LocalDate.parse(stat.time, formatter) }
                        .mapValues { (_, stats) -> stats.sumOf(valueSelector) }

                    // Создаем полный список дней в диапазоне с нулями, если данных нет
                    val fullList = mutableListOf<Pair<Long, Int>>()
                    val fromDate = LocalDate.parse(from.substring(0, 10))
                    val toDate = LocalDate.parse(to.substring(0, 10))

                    var currentDate = fromDate
                    while (!currentDate.isAfter(toDate)) {
                        val timestamp = currentDate
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
                        val value = grouped[currentDate] ?: 0
                        fullList.add(timestamp to value)
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