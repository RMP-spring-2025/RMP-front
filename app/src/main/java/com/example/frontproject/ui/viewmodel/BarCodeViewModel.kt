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
import com.example.frontproject.data.repository.MealsRepository // Убедитесь, что путь правильный
import com.example.frontproject.domain.util.ResourceState
import com.example.frontproject.ui.components.screens.lastBarcodeValue

class BarCodeViewModel(
    private val repository: MealsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BarcodeUiState>(BarcodeUiState.Scanning)
    val uiState: StateFlow<BarcodeUiState> = _uiState

    private var currentBarcodeJob: String? = null

    fun checkProduct(barcodeValue: String) {
        if (_uiState.value is BarcodeUiState.Loading && currentBarcodeJob == barcodeValue) {
            Log.d("BarCodeViewModel", "Already processing barcode: $barcodeValue")
            return
        }
        currentBarcodeJob = barcodeValue
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
                    _uiState.value = BarcodeUiState.Loading
                }
            }
            if (_uiState.value !is BarcodeUiState.Loading) {
                currentBarcodeJob = null
            }
        }
    }

    fun searchProductByName(name: String) {
        viewModelScope.launch {
            _uiState.value = BarcodeUiState.SearchingByName
            when (val result = repository.getProductsByName(name)) {
                is ResourceState.Success -> {
                    if (result.data.isNotEmpty()) {
                        _uiState.value = BarcodeUiState.ProductsFoundByName(result.data)
                    } else {
                        _uiState.value = BarcodeUiState.NoProductsFoundByName(name)
                    }
                }
                is ResourceState.Error -> {
                    _uiState.value = BarcodeUiState.Error(result.message)
                }
                is ResourceState.Loading -> {
                    _uiState.value = BarcodeUiState.SearchingByName
                }
            }
        }
    }

    fun manualProductSelect(product: Product) {
        // Устанавливаем выбранный продукт так, как будто он был найден по штрих-коду
        _uiState.value = BarcodeUiState.ProductFound(ResourceState.Success(product))
    }

    fun resetState() {
        _uiState.value = BarcodeUiState.Scanning
        currentBarcodeJob = null
        lastBarcodeValue = null // Если вы используете эту глобальную переменную
    }

    fun addProductWithMass(product: Product, mass: Float, time: String) {
        viewModelScope.launch {
            _uiState.value = BarcodeUiState.Loading // Можно использовать SearchingByName или оставить Loading
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

    fun addNewProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = BarcodeUiState.Loading
            Log.d("BarCodeViewModel", "addNewProduct: $product")
            when (val response = repository.addProduct(product)) { // Предполагается, что этот метод есть
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
         resetState()
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