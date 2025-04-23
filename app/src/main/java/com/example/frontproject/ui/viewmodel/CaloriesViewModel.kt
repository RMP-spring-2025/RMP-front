package com.example.frontproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontproject.data.model.DayMeals
import com.example.frontproject.data.repository.MealsRepository
import com.example.frontproject.domain.util.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CaloriesViewModel : ViewModel() {
    private val repository = MealsRepository()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _currentMonth = MutableStateFlow(YearMonth.from(LocalDate.now()))
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    private val _dayMeals = MutableStateFlow<ResourceState<DayMeals>>(ResourceState.Loading)
    val dayMeals: StateFlow<ResourceState<DayMeals>> = _dayMeals

    init {
        loadDataForSelectedDate()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadDataForSelectedDate()
    }

    fun changeMonth(yearMonth: YearMonth) {
        _currentMonth.value = yearMonth
        // Устанавливаем выбранную дату на первый день месяца или сегодня, если это текущий месяц
        val today = LocalDate.now()
        val newSelectedDate = if (YearMonth.from(today) == yearMonth && today.monthValue == yearMonth.monthValue) {
            today
        } else {
            yearMonth.atDay(1)
        }
        _selectedDate.value = newSelectedDate
        loadDataForSelectedDate()
    }

    fun loadDataForSelectedDate() {
        viewModelScope.launch {
            try {
                _dayMeals.value = ResourceState.Loading
                val formattedDate = selectedDate.value.format(DateTimeFormatter.ISO_DATE)

                // Используем formattedDate вместо date и обновляем _dayMeals вместо _uiState
                repository.getMealsByDay(formattedDate).collect { state ->
                    _dayMeals.value = state
                }
            } catch (e: Exception) {
                _dayMeals.value = ResourceState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
}