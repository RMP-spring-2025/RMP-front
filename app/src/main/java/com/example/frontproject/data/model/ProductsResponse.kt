package com.example.frontproject.data.model

import com.google.gson.annotations.SerializedName


data class ProductItem(
    val time: String, // Формат "yyyy-MM-ddTHH:mm:ss"
    val name: String,
    val calories: Int,
    @SerializedName("B") // Маппинг поля "B" из JSON на поле proteins
    val proteins: Float,
    @SerializedName("Z") // Маппинг поля "Z" из JSON на поле fats
    val fats: Float,
    @SerializedName("U") // Маппинг поля "U" из JSON на поле carbs
    val carbs: Float,
    val massConsumed: Int // Или Float/Double, если может быть дробным
)

// Новая вложенная модель для данных о продукте
data class ProductDetails(
    val requestId: String,
    val productId: Int,
    val name: String,
    val calories: Float,
    @SerializedName("B") val proteins: Float,
    @SerializedName("Z") val fats: Float,
    @SerializedName("U") val carbs: Float,
    val mass: Float
)

// Обновленная модель для ответа при поиске по штрихкоду
data class ProductResponseByBcode(
    val status: String,
    val data: ProductDetails? = null,
    val errorMessage: String? = null
)

// Модель для всего ответа от /heavy_response/{id} для списка продуктов
data class ProductsResponse(
    val stats: List<ProductItem>
)