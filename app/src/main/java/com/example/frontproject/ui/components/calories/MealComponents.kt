package com.example.frontproject.ui.components.calories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontproject.data.model.DayMeals
import com.example.frontproject.data.model.Meal

@Composable
fun MealSection(title: String, meals: List<Meal>) {
    val totalCalories = meals.sumOf { it.calories }

    // Card or Column
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF333333)
                )

                Text(
                    text = "${meals.size} ${MealsRightForm(meals.size)} | $totalCalories ккал",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W500,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (meals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Нет записей о приеме пищи",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                meals.forEachIndexed { index, meal ->
                    MealItem(meal)
                    if (index < meals.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFFEEEEEE)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MealItem(meal: Meal) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Плейсхолдер для изображения блюда
        // Заменить на AsyncImage или Image с загрузкой изображений из API.
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = meal.name.take(1),
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = meal.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NutrientBadge("Б: ${meal.proteins}г", Color(0xFF64B5F6))
                Spacer(modifier = Modifier.width(8.dp))
                NutrientBadge("Ж: ${meal.fats}г", Color(0xFFFFB74D))
                Spacer(modifier = Modifier.width(8.dp))
                NutrientBadge("У: ${meal.carbs}г", Color(0xFF81C784))
            }
        }
    }
}

@Composable
fun NutrientBadge(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )

        Text(
            text = text,
            modifier = Modifier.padding(start = 4.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun DailyCaloriesSummary(meals: DayMeals) {
    val totalCalories = meals.breakfast.sumOf { it.calories } +
            meals.lunch.sumOf { it.calories } +
            meals.snacks.sumOf { it.calories } +
            meals.dinner.sumOf { it.calories }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Всего калорий за день",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Text(
            text = "$totalCalories ккал",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF986ef2)
        )
    }
}

fun MealsRightForm(count: Int): String {
    val meals = when (count) {
        1 -> "блюдо"
        2, 3, 4 -> "блюда"
        else -> "блюд"
    }
    return meals
}