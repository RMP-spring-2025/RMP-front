package com.example.frontproject.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frontproject.data.repository.HealthConnectAvailability
import com.example.frontproject.data.repository.HealthConnectRepository
import com.example.frontproject.domain.util.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StepsUiState(
    val steps: Int = 0,
    val goal: Int = STEPS_GOAL_DEFAULT,
    val isLoading: Boolean = false,
    val error: String? = null,
    val permissionsGranted: Boolean = false,
    val healthConnectAvailability: HealthConnectAvailability = HealthConnectAvailability.NOT_SUPPORTED
)

const val STEPS_GOAL_DEFAULT = 10000

class StepsViewModel(
    val healthConnectRepository: HealthConnectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StepsUiState(goal = STEPS_GOAL_DEFAULT))
    val uiState: StateFlow<StepsUiState> = _uiState.asStateFlow()

    // Для запуска запроса разрешений из Composable
    val requestPermissionsActivityContract =
        PermissionController.createRequestPermissionResultContract()
    val showPermissionRationale = mutableStateOf(false)


    init {
        loadStepsData()
    }

    fun loadStepsData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val availability = healthConnectRepository.getSdkStatus()
            _uiState.value = _uiState.value.copy(healthConnectAvailability = availability)

            if (availability == HealthConnectAvailability.AVAILABLE) {
                val hasPermissions = healthConnectRepository.checkPermissions()
                _uiState.value = _uiState.value.copy(permissionsGranted = hasPermissions)

                if (hasPermissions) {
                    when (val result = healthConnectRepository.getTodaySteps()) {
                        is ResourceState.Success -> {
                            _uiState.value = _uiState.value.copy(
                                steps = result.data.toInt(),
                                isLoading = false
                            )
                        }
                        is ResourceState.Error -> {
                            _uiState.value = _uiState.value.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                        is ResourceState.Loading -> {
                        }
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Разрешения не предоставлены. Нажмите, чтобы запросить.",
                        isLoading = false
                    )
                    showPermissionRationale.value = true
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    error = when(availability) {
                        HealthConnectAvailability.NOT_INSTALLED -> "Health Connect не установлен."
                        HealthConnectAvailability.NOT_SUPPORTED -> "Health Connect не поддерживается на этом устройстве."
                        else -> "Health Connect недоступен."
                    },
                    isLoading = false
                )
            }
        }
    }

    fun getPermissionsLaunchIntent() = requestPermissionsActivityContract

    companion object {
        fun provideFactory(
            healthConnectRepository: HealthConnectRepository,
            application: Application
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(StepsViewModel::class.java)) {
                    return StepsViewModel(healthConnectRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}