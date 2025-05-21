package com.example.frontproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frontproject.data.repository.UserProfileRepository
import com.example.frontproject.ui.model.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            // ProfileUiState.Loading будет установлено из UserProfileRepository, если кеша нет
            userProfileRepository.getUserProfileStats()
                .catch { e ->
                    // Обработка ошибок, если сам Flow завершился с ошибкой
                    _uiState.value =
                        ProfileUiState.Error("Ошибка загрузки профиля: ${e.localizedMessage}")
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    companion object {
        fun provideFactory(
            userProfileRepository: UserProfileRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    return HomeViewModel(userProfileRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}