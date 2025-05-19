package com.example.frontproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frontproject.data.repository.UserProfileRepository // Импортируем репозиторий
import com.example.frontproject.ui.model.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userProfileRepository: UserProfileRepository // Заменяем apiRequestExecutor на репозиторий
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun fetchUserProfileStats() {
        viewModelScope.launch {
            userProfileRepository.getUserProfileStats()
                .catch { e -> // Ловим непредвиденные ошибки из самого flow
                    _uiState.value = ProfileUiState.Error("Ошибка при загрузке данных: ${e.localizedMessage}")
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun resetState() {
        _uiState.value = ProfileUiState.Idle
        // Если нужно также очищать кеш в репозитории при сбросе состояния:
        // userProfileRepository.clearCache()
    }

    companion object {
        fun provideFactory(
            userProfileRepository: UserProfileRepository // Изменяем параметр фабрики
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                    return ProfileViewModel(userProfileRepository) as T // Передаем репозиторий
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}