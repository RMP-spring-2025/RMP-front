package com.example.frontproject.ui.viewmodel.carb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frontproject.data.repository.BzuRepository
import com.example.frontproject.domain.util.ResourceState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CarbHistoryViewModel(
    private val repository: BzuRepository
) : ViewModel() {

    private val _carbHistoryState =
        MutableStateFlow<ResourceState<List<Pair<Long, Int>>>>(ResourceState.Loading)
    val carbHistoryState: StateFlow<ResourceState<List<Pair<Long, Int>>>> = _carbHistoryState

    init {
        loadCarbForLast30Days()
    }

    private fun loadCarbForLast30Days() {
        viewModelScope.launch {
            _carbHistoryState.value = ResourceState.Loading

            val today = LocalDate.now()
            val fromDate = today.minusDays(29) // 30 дней включая сегодня
            val toDate = today

            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val from = fromDate.atStartOfDay().format(formatter)
            val to = toDate.atTime(23, 59, 59).format(formatter)

            _carbHistoryState.value = repository.getCarbsArray(from, to)
        }
    }

    companion object {
        fun provideFactory(repository: BzuRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CarbHistoryViewModel(repository) as T
                }
            }
    }
}