package com.example.frontproject.ui.viewmodel // Убедитесь, что пакет правильный

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frontproject.api.ApiRequestExecutor
import com.example.frontproject.data.model.AuthRequest
import com.example.frontproject.domain.util.ResourceState
import com.example.frontproject.store.SettingsRepository
import com.example.frontproject.store.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SaveState {
    data object Idle : SaveState()
    data object Success : SaveState()
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Success : AuthState()
    data class Error(val message: String) : AuthState()
    data object LogoutSuccess : AuthState()
}

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val tokenRepository: TokenRepository,
    private val apiRequestExecutor: ApiRequestExecutor
) : ViewModel() {

    private val _currentBaseUrl = MutableStateFlow(settingsRepository.getBaseUrl() ?: SettingsRepository.DEFAULT_BASE_URL)
    val currentBaseUrl: StateFlow<String> = _currentBaseUrl.asStateFlow()

    private val _newBaseUrl = MutableStateFlow(settingsRepository.getBaseUrl() ?: SettingsRepository.DEFAULT_BASE_URL)
    val newBaseUrl: StateFlow<String> = _newBaseUrl.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun onNewUrlChange(url: String) {
        _newBaseUrl.value = url
    }

    fun saveBaseUrl() {
        viewModelScope.launch {
            val urlToSave = _newBaseUrl.value.ifEmpty { SettingsRepository.DEFAULT_BASE_URL }
            settingsRepository.saveBaseUrl(urlToSave)
            _currentBaseUrl.value = urlToSave
            _saveState.value = SaveState.Success
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val authRequest = AuthRequest(username, password)
            when (val response = apiRequestExecutor.executeRequest { apiRequestExecutor.apiService.login(authRequest) }) {
                is ResourceState.Success -> {
                    val authResponse = response.data
                    tokenRepository.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                    _authState.value = AuthState.Success
                }
                is ResourceState.Error -> {
                    _authState.value = AuthState.Error(response.message)
                }
                is ResourceState.Loading -> {
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenRepository.clearTokens()
            _authState.value = AuthState.LogoutSuccess
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}

// Фабрика для SettingsViewModel
class SettingsViewModelFactory(
    private val settingsRepository: SettingsRepository,
    private val tokenRepository: TokenRepository,
    private val apiRequestExecutor: ApiRequestExecutor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsRepository, tokenRepository, apiRequestExecutor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}