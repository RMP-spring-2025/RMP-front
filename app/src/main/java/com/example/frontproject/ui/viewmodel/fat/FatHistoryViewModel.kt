package com.example.frontproject.ui.viewmodel.fat

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

class FatHistoryViewModel(
    private val repository: BzuRepository
) : ViewModel() {

    private val _fatHistoryState =
        MutableStateFlow<ResourceState<List<Pair<Long, Int>>>>(ResourceState.Loading)
    val fatHistoryState: StateFlow<ResourceState<List<Pair<Long, Int>>>> = _fatHistoryState

    init {
        loadFatForLast30Days()
    }

    private fun loadFatForLast30Days() {
        viewModelScope.launch {
            _fatHistoryState.value = ResourceState.Loading

            val today = LocalDate.now()
            val fromDate = today.minusDays(29) // 30 дней включая сегодня
            val toDate = today

            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val from = fromDate.atStartOfDay().format(formatter)
            val to = toDate.atTime(23, 59, 59).format(formatter)

            _fatHistoryState.value = repository.getFatArray(from, to)
        }
    }

    companion object {
        fun provideFactory(repository: BzuRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FatHistoryViewModel(repository) as T
                }
            }
    }
}