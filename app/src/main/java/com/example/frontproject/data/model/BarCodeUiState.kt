package com.example.frontproject.data.model

import com.example.frontproject.domain.util.ResourceState

sealed class BarcodeUiState {
    object Scanning : BarcodeUiState()
    object Loading : BarcodeUiState()
    data class ProductFound(val product: ResourceState<Product>) : BarcodeUiState()
    data class ProductNotFound(val barcode: String) : BarcodeUiState()
    object ProductAdded : BarcodeUiState()
    data class Error(val message: String) : BarcodeUiState()
}