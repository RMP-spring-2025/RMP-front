package com.example.frontproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frontproject.data.model.product.Product
import com.example.frontproject.data.repository.MealsRepository
import com.example.frontproject.domain.util.ResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SearchScreenUiState {
    object Idle : SearchScreenUiState()
    object Loading : SearchScreenUiState()
    data class Success(val products: List<Product>) : SearchScreenUiState()
    data class NoResults(val query: String) : SearchScreenUiState()
    data class Error(val message: String) : SearchScreenUiState()
}

class SearchViewModel(private val mealsRepository: MealsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchScreenUiState>(SearchScreenUiState.Idle)
    val uiState: StateFlow<SearchScreenUiState> = _uiState.asStateFlow()

    fun searchProductByName(name: String) {
        if (name.isBlank()) {
            _uiState.value = SearchScreenUiState.Idle // Или можете установить состояние ошибки для пустого запроса
            return
        }
        viewModelScope.launch {
            _uiState.value = SearchScreenUiState.Loading
            when (val result = mealsRepository.getProductsByName(name)) {
                is ResourceState.Success -> {
                    if (result.data.isNotEmpty()) {
                        _uiState.value = SearchScreenUiState.Success(result.data)
                    } else {
                        _uiState.value = SearchScreenUiState.NoResults(name)
                    }
                }
                is ResourceState.Error -> {
                    _uiState.value = SearchScreenUiState.Error(result.message)
                }
                is ResourceState.Loading -> {
                    // Это состояние обычно обрабатывается внутри repository,
                    // но на всякий случай можно оставить
                    _uiState.value = SearchScreenUiState.Loading
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = SearchScreenUiState.Idle
    }

    companion object {
        fun provideFactory(
            mealsRepository: MealsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                    return SearchViewModel(mealsRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}