package com.example.frontproject.ui.viewmodel.calories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frontproject.data.repository.BzuRepository
import com.example.frontproject.data.repository.CaloriesRepository
import com.example.frontproject.domain.util.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CarbTodayViewModel(
    private val repository: BzuRepository
) : ViewModel() {

    private val _carbState = MutableStateFlow<ResourceState<Int>>(ResourceState.Loading)
    val carbState: StateFlow<ResourceState<Int>> = _carbState

    init {
        loadCarbForToday()
    }

    fun loadCarbForToday() {
        viewModelScope.launch {
            _carbState.value = ResourceState.Loading

            val today = LocalDate.now()
            val from = today.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val to = today.atTime(23, 59, 59).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            _carbState.value = repository.getCarbsForDate(from, to)
        }
    }

    companion object {
        fun provideFactory(repository: BzuRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CarbTodayViewModel(repository) as T
                }
            }
    }
}
