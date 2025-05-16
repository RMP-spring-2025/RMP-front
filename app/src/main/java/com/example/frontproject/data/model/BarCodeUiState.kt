package com.example.frontproject.data.model

import com.example.frontproject.domain.util.ResourceState


sealed class BarcodeUiState {
    object Scanning : BarcodeUiState()
    object Loading : BarcodeUiState() // Общая загрузка
    object SearchingByName : BarcodeUiState() // Загрузка при поиске по имени
    data class ProductFound(val product: ResourceState<Product>) : BarcodeUiState()
    data class ProductsFoundByName(val products: List<Product>) : BarcodeUiState() // Список продуктов, найденных по имени
    data class ProductNotFound(val identifier: String) : BarcodeUiState() // Может использоваться и для штрихкода
    data class NoProductsFoundByName(val query: String) : BarcodeUiState() // Ничего не найдено по имени
    object ProductAdded : BarcodeUiState()
    data class Error(val message: String) : BarcodeUiState()
}