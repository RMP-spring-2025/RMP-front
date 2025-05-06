package com.example.frontproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontproject.data.model.BarcodeUiState
import com.example.frontproject.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.frontproject.data.model.ConsumeProductRequest
import com.example.frontproject.data.repository.MealsRepository
import com.example.frontproject.domain.util.ResourceState
import com.example.frontproject.ui.components.screens.lastBarcodeValue

class BarCodeViewModel(
    private val repository: MealsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BarcodeUiState>(BarcodeUiState.Scanning)
    val uiState: StateFlow<BarcodeUiState> = _uiState

    // Хранение последнего обрабатываемого штрих-кода
    private var currentBarcodeJob: String? = null

    // Обработка отсканированного штрих-кода
    fun checkProduct(barcodeValue: String) {
        // Если уже идет загрузка для этого же штрих-кода, ничего не делаем
        if (_uiState.value is BarcodeUiState.Loading && currentBarcodeJob == barcodeValue) {
            Log.d("BarCodeViewModel", "Already processing barcode: $barcodeValue")
            return
        }
        // Если состояние - не сканирование и не ошибка (т.е. продукт найден/не найден/добавлен),
        // и сканируется тот же самый штрих-код, не делаем новый запрос,
        // чтобы избежать повторной обработки уже известного результата без сброса.
        // Это поведение можно настроить в зависимости от требований.
        // В данном случае, мы позволяем повторный поиск, если состояние было сброшено на Scanning
        // или если это новый штрих-код.

        currentBarcodeJob = barcodeValue // Запоминаем текущий обрабатываемый штрих-код
        viewModelScope.launch {
            _uiState.value = BarcodeUiState.Loading
            when (val productResult = repository.getProductByBarcode(barcodeValue)) {
                is ResourceState.Success -> {
                    _uiState.value = BarcodeUiState.ProductFound(productResult)
                }

                is ResourceState.Error -> {
                    _uiState.value = BarcodeUiState.ProductNotFound(barcodeValue)
                }

                is ResourceState.Loading -> {
                    // Это состояние уже установлено, но на всякий случай
                    _uiState.value = BarcodeUiState.Loading
                }
            }
            // Сбрасываем currentBarcodeJob после завершения, если это необходимо,
            // или оставляем, чтобы предотвратить повторный запрос до resetState()
            // В данном случае, лучше сбросить, чтобы следующий скан того же кода после результата (не Loading) прошел
//             if (_uiState.value !is BarcodeUiState.Loading) {
//                 currentBarcodeJob = null
//             }
        }
    }

    // Сброс состояния для повторного сканирования
    fun resetState() {
        _uiState.value = BarcodeUiState.Scanning
        currentBarcodeJob = null
        lastBarcodeValue = null
    }

    // Добавление продукта с массой
    fun addProductWithMass(product: Product, mass: Float, time: String) {
        viewModelScope.launch {
            _uiState.value = BarcodeUiState.Loading
            val request = ConsumeProductRequest(
                productId = product.id,
                time = time,
                massConsumed = mass.toInt()
            )
            when (val result = repository.consumeProduct(request)) {
                is ResourceState.Success -> _uiState.value = BarcodeUiState.ProductAdded
                is ResourceState.Error -> _uiState.value = BarcodeUiState.Error(result.message)
                ResourceState.Loading -> _uiState.value = BarcodeUiState.Loading
            }
        }
    }

    // Добавление нового продукта
    fun addNewProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = BarcodeUiState.Loading
            Log.d("BarCodeViewModel", "addNewProduct: $product")
            when (val response = repository.addProduct(product)) {
                is ResourceState.Success -> {
                    _uiState.value = BarcodeUiState.ProductAdded
                }

                is ResourceState.Error -> {
                    _uiState.value = BarcodeUiState.Error(response.message)
                }

                is ResourceState.Loading -> {
                    // UI останется в загрузке.
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            mealsRepository: MealsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(BarCodeViewModel::class.java)) {
                    return BarCodeViewModel(mealsRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}