package com.example.frontproject.data.model

data class DayMeals(
    val date: String,
    val breakfast: List<Meal>,
    val lunch: List<Meal>,
    val snacks: List<Meal>,
    val dinner: List<Meal>
)