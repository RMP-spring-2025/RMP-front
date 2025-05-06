package com.example.frontproject.data.model

// Структура для хранения сгруппированных по времени приемов пищи
data class GroupedMeals(
    val breakfast: List<Meal> = emptyList(),
    val lunch: List<Meal> = emptyList(),
    val snacks: List<Meal> = emptyList(),
    val dinner: List<Meal> = emptyList()
)