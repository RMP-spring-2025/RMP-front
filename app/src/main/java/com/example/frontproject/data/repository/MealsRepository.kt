package com.example.frontproject.data.repository

import com.example.frontproject.data.model.DayMeals
import com.example.frontproject.data.model.Meal
import com.example.frontproject.data.remote.RetrofitInstance
import com.example.frontproject.domain.util.ResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MealsRepository {
    private val api = RetrofitInstance.api
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getMealsByDayRange(
        from: String,
        to: String
    ): Flow<ResourceState<DayMeals>> = flow {
        emit(ResourceState.Loading)

        try {
            // Инициируем запрос и получаем ID
            val requestIdResponse = api.initProductsRequest(from, to)
            val requestId = requestIdResponse.id

            // Периодически опрашиваем сервер
            var response: Response<DayMeals>? = null
            var attempts = 0

            while (attempts < 10) {  // Максимум 10 попыток
                delay(2000L)  // Ждем 2 секунды

                response = api.getHeavyResponse(requestId)

                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(ResourceState.Success(it))
                        return@flow
                    }
                }

                attempts++
            }

            emit(ResourceState.Error("Превышено время ожидания ответа"))
        } catch (e: Exception) {
            emit(ResourceState.Error("Ошибка: ${e.localizedMessage}"))
        }
    }

    suspend fun getMealsByDay(date: String): Flow<ResourceState<DayMeals>> {
        // Запрашиваем данные за один день
        return getMealsByDayRange(date, date)
    }
}

//    fun getMealsByDay(date: String): DayMeals {
//
//        val today = LocalDate.now()
//        // Mock data for demonstration purposes
//        val todayMeals = DayMeals(
//            date = today.format(DateTimeFormatter.ISO_DATE),
//            breakfast = listOf(
//                Meal("Омлет из нестоловой", 228, 14, 88, 0),
//                Meal("Цельнозерновой тост из нестоловой", 42, 52, 13, 37)
//            ),
//            lunch = listOf(
//                Meal("Куриный суп из нестоловой", 180, 15, 8, 12),
//                Meal("Салат с тунцом из нестоловой", 250, 22, 12, 8),
//                Meal("Гречка из нестоловой", 150, 5, 1, 30)
//            ),
//            snacks = listOf(
//                Meal("Хер с маслом", 1, 2, 3, 4),
//            ),
//            dinner = listOf(
//                Meal("Закончились идеи", 555, 123, 321, 228),
//            )
//        )
//        val prevDayMeals = DayMeals(
//            date = today.minusDays(1).format(DateTimeFormatter.ISO_DATE),
//            breakfast = listOf(
//                Meal("Омлет с овощами", 220, 18, 15, 6),
//                Meal("Цельнозерновой тост", 120, 4, 2, 20)
//            ),
//            lunch = listOf(
//                Meal("Куриный суп", 180, 15, 8, 12),
//                Meal("Салат с тунцом", 250, 22, 12, 8),
//                Meal("Гречка", 150, 5, 1, 30)
//            ),
//            snacks = listOf(
//                Meal("Яблоко", 80, 0, 0, 20),
//                Meal("Греческий йогурт", 120, 12, 3, 5)
//            ),
//            dinner = listOf(
//                Meal("Лосось на гриле", 300, 28, 18, 0),
//                Meal("Печеные овощи", 120, 3, 4, 15)
//            )
//        )
//        return if (date == today.format(DateTimeFormatter.ISO_DATE)) {
//            todayMeals
//        } else if (date == today.minusDays(1).format(DateTimeFormatter.ISO_DATE)) {
//            prevDayMeals
//        } else {
//            DayMeals(date, emptyList(), emptyList(), emptyList(), emptyList())
//        }
//    }