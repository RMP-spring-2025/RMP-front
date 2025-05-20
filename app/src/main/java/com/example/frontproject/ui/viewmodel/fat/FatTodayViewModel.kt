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

class FatTodayViewModel(
    private val repository: BzuRepository
) : ViewModel() {

    private val _fatState = MutableStateFlow<ResourceState<Int>>(ResourceState.Loading)
    val fatState: StateFlow<ResourceState<Int>> = _fatState

    init {
        loadFatForToday()
    }

    fun loadFatForToday() {
        viewModelScope.launch {
            _fatState.value = ResourceState.Loading

            val today = LocalDate.now()
            val from = today.atStartOfDay().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val to = today.atTime(23, 59, 59).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            _fatState.value = repository.getFatsForDate(from, to)
        }
    }

    companion object {
        fun provideFactory(repository: BzuRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FatTodayViewModel(repository) as T
                }
            }
    }
}
