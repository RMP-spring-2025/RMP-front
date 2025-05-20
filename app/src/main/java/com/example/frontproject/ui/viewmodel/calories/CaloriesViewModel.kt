package com.example.frontproject.ui.viewmodel.calories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frontproject.data.model.meal.GroupedMeals // <-- Импорт GroupedMeals
import com.example.frontproject.data.model.meal.Meal
import com.example.frontproject.data.model.product.ProductItem
import com.example.frontproject.data.model.product.ProductsResponse
import com.example.frontproject.data.repository.MealsRepository
import com.example.frontproject.domain.util.ResourceState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime // <-- Импорт LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt // <-- Импорт для округления

class CaloriesViewModel(
    private val repository: MealsRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _currentMonth = MutableStateFlow(YearMonth.from(LocalDate.now()))
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    private val _groupedMealsState = MutableStateFlow<ResourceState<GroupedMeals>>(ResourceState.Loading)
    val groupedMealsState: StateFlow<ResourceState<GroupedMeals>> = _groupedMealsState


    init {
        loadDataForSelectedDate()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        loadDataForSelectedDate()
    }

    fun changeMonth(yearMonth: YearMonth) {
        _currentMonth.value = yearMonth
        val today = LocalDate.now()
        val newSelectedDate =
            if (YearMonth.from(today) == yearMonth && today.monthValue == yearMonth.monthValue) {
                today
            } else {
                yearMonth.atDay(1)
            }
        _selectedDate.value = newSelectedDate
        loadDataForSelectedDate()
    }

    fun loadDataForSelectedDate() {
        viewModelScope.launch {
            _groupedMealsState.value = ResourceState.Loading // Устанавливаем Loading

            val startOfDay = selectedDate.value.atStartOfDay()
            val endOfDay = selectedDate.value.atTime(23, 59, 59)
            // Форматируем даты в нужный формат
            // Пример: 2025-05-04T20:36:44
            val fromDateTime = startOfDay.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val toDateTime = endOfDay.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            // Получаем ResourceState<StatsResponse> из репозитория
            val statsResult = repository.getProductsByDateRange(fromDateTime, toDateTime)

            // Преобразуем ResourceState<StatsResponse> в ResourceState<GroupedMeals>
            _groupedMealsState.value = when (statsResult) {
                is ResourceState.Success -> {
                    // Группируем StatItem в GroupedMeals
                    val groupedData = groupStatsToMeals(statsResult.data)
                    ResourceState.Success(groupedData)
                }
                is ResourceState.Error -> {
                    // Передаем ошибку дальше
                    ResourceState.Error(statsResult.message)
                }
                is ResourceState.Loading -> {
                    ResourceState.Loading
                }
            }
        }
    }

    private fun ProductItem.toMeal(): Meal {
        return Meal(
            name = this.name,
            calories = this.calories,
            proteins = this.proteins.roundToInt(),
            fats = this.fats.roundToInt(),
            carbs = this.carbs.roundToInt(),
            massConsumed = this.massConsumed
        )
    }

    private fun groupStatsToMeals(productsResponse: ProductsResponse): GroupedMeals {
        val breakfast = mutableListOf<Meal>()
        val lunch = mutableListOf<Meal>()
        val snacks = mutableListOf<Meal>()
        val dinner = mutableListOf<Meal>()

        val noon = LocalTime.of(12, 0)
        val threePm = LocalTime.of(15, 0)
        val sixPm = LocalTime.of(18, 0)

        productsResponse.stats.forEach { statItem ->
            try {
                val dateTime = LocalDateTime.parse(statItem.time, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                val time = dateTime.toLocalTime()
                val meal = statItem.toMeal()

                when {
                    time.isBefore(noon) -> breakfast.add(meal)
                    time.isBefore(threePm) -> lunch.add(meal)
                    time.isBefore(sixPm) -> snacks.add(meal)
                    else -> dinner.add(meal)
                }
            } catch (e: Exception) {
                println("Error parsing time for StatItem: ${statItem.time}")
            }
        }

        return GroupedMeals(breakfast, lunch, snacks, dinner)
    }


    companion object {
        fun provideFactory(
            mealsRepository: MealsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CaloriesViewModel::class.java)) {
                    return CaloriesViewModel(mealsRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}