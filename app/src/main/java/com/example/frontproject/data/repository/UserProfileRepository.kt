package com.example.frontproject.data.repository

import com.example.frontproject.api.ApiRequestExecutor
import com.example.frontproject.data.model.stats.UserProfileStatsResponse
import com.example.frontproject.domain.util.ResourceState
import com.example.frontproject.ui.model.ProfileUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserProfileRepository(
    private val apiRequestExecutor: ApiRequestExecutor
) {
    private var cachedUserProfile: UserProfileStatsResponse? = null

    fun getUserProfileStats(): Flow<ProfileUiState> = flow {
        // 1. Сообщаем о начале загрузки
        emit(ProfileUiState.Loading)

        // 2. Отдаем кешированные данные, если они есть
        cachedUserProfile?.let {
            emit(ProfileUiState.Success(it))
        }

        // 3. Запрашиваем свежие данные из сети
        when (val result = apiRequestExecutor.executeHeavyRequest(
            initialCall = { apiRequestExecutor.apiService.getUserStats() },
            pollingCall = { requestId -> apiRequestExecutor.apiService.getUserStatsResponse(requestId) }
        )) {
            is ResourceState.Success -> {
                cachedUserProfile = result.data // Обновляем кеш
                emit(ProfileUiState.Success(result.data)) // Отдаем свежие данные
            }
            is ResourceState.Error -> {
                // Если есть ошибка, сообщаем о ней.
                // Если ранее были отданы кешированные данные, UI может их все еще показывать
                // или заменить на сообщение об ошибке, в зависимости от реализации экрана.
                // Текущая реализация ProfileScreen заменит содержимое на сообщение об ошибке.
                emit(ProfileUiState.Error(result.message))
            }
            is ResourceState.Loading -> {
                // Это состояние обрабатывается начальным emit(ProfileUiState.Loading)
                // и executeHeavyRequest сам по себе не возвращает ResourceState.Loading своему вызывающему.
            }
        }
    }

    // Опционально: метод для очистки кеша, например, при выходе из системы
    fun clearCache() {
        cachedUserProfile = null
    }
}