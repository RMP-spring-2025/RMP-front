package com.example.frontproject.data.model

data class Product(
    val id: Int,
    val name: String,
    val barcode: Long,
    val calories: Int,
    val proteins: Float,
    val fats: Float,
    val carbs: Float,
    val mass: Int? = 0 // Масса может быть 0, если не указана
)

data class AddProductRequest(
    val name: String,
    val bcode: Long, // Штрих-код, может быть null если не указан
    val B: Float,       // Белки
    val Z: Float,       // Жиры
    val U: Float,       // Углеводы
    val calories: Int,
    val mass: Int?      // Масса, может быть null
)