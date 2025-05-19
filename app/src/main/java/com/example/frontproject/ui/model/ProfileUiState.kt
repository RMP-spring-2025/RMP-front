package com.example.frontproject.ui.model

import com.example.frontproject.data.model.stats.UserProfileStatsResponse
import com.example.frontproject.domain.util.ResourceState

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(val userProfile: UserProfileStatsResponse) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    data object Idle : ProfileUiState()
}