package com.example.frontproject.ui.viewmodel.calories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frontproject.data.repository.CaloriesRepository
import com.example.frontproject.domain.util.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CaloriesHistoryViewModel(
    private val repository: CaloriesRepository
) : ViewModel() {

    private val _caloriesHistoryState =
        MutableStateFlow<ResourceState<List<Pair<Long, Int>>>>(ResourceState.Loading)
    val caloriesHistoryState: StateFlow<ResourceState<List<Pair<Long, Int>>>> = _caloriesHistoryState

    init {
        loadCaloriesForLast30Days()
    }

    private fun loadCaloriesForLast30Days() {
        viewModelScope.launch {
            _caloriesHistoryState.value = ResourceState.Loading

            val today = LocalDate.now()
            val fromDate = today.minusDays(29) // включая сегодня, итого 30 дней
            val toDate = today

            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val from = fromDate.atStartOfDay().format(formatter)
            val to = toDate.atTime(23, 59, 59).format(formatter)

            _caloriesHistoryState.value = repository.getCaloriesArray(from, to)
        }
    }

    companion object {
        fun provideFactory(repository: CaloriesRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CaloriesHistoryViewModel(repository) as T
                }
            }
    }
}

