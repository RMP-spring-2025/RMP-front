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

class CaloriesTodayViewModel(
    private val repository: CaloriesRepository
) : ViewModel() {

    private val _caloriesState = MutableStateFlow<ResourceState<Int>>(ResourceState.Loading)
    val caloriesState: StateFlow<ResourceState<Int>> = _caloriesState

    init {
        loadCaloriesForToday()
    }

    fun loadCaloriesForToday() {
        viewModelScope.launch {
            _caloriesState.value = ResourceState.Loading

            val today = LocalDate.now()
            val from = today.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val to = today.atTime(23, 59, 59).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            _caloriesState.value = repository.getCaloriesForDate(from, to)
        }
    }

    companion object {
        fun provideFactory(repository: CaloriesRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CaloriesTodayViewModel(repository) as T
                }
            }
    }
}
