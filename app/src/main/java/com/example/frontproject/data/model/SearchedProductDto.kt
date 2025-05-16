package com.example.frontproject.data.model

import com.google.gson.annotations.SerializedName

data class SearchedProductDto(
    val productId: Int,
    val name: String,
    val calories: Double,
    @SerializedName("B") val protein: Double, // Белки
    @SerializedName("Z") val fat: Double,     // Жиры
    @SerializedName("U") val carbs: Double,   // Углеводы
    val mass: Double
)

data class ProductsByNameDataDto(
    val requestId: String,
    val products: List<SearchedProductDto>
)

data class ProductsByNameResponseDto(
    val status: String?,
    val errorMessage: String?,
    val data: ProductsByNameDataDto?
)