package com.example.frontproject.ui.components.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontproject.MyApp
import com.example.frontproject.RmpApplication
import com.example.frontproject.domain.util.ResourceState
import com.example.frontproject.ui.components.calories.DateSelector
import com.example.frontproject.ui.components.calories.DailyMealsSummary
import com.example.frontproject.ui.components.calories.MealSection
import com.example.frontproject.ui.components.calories.MonthSelector
import com.example.frontproject.ui.components.common.ScreenHeader
import com.example.frontproject.ui.viewmodel.CaloriesViewModel

@Composable
fun CaloriesScreen(
    navController: NavController,
    viewModel: CaloriesViewModel = viewModel(
        factory = CaloriesViewModel.provideFactory(
            (LocalContext.current.applicationContext as RmpApplication).appContainer.mealsRepository
        )
    )
) {
    val selectedDate = viewModel.selectedDate.collectAsState().value
    val currentMonth = viewModel.currentMonth.collectAsState().value
    val mealsState = viewModel.groupedMealsState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
    ) {
        ScreenHeader(
            title = "Калории",
            onBackClick = { navController.popBackStack() },
        )

        MonthSelector(
            currentMonth = currentMonth,
            onMonthChanged = { viewModel.changeMonth(it) }
        )

        DateSelector(
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            onDateSelected = { viewModel.selectDate(it) }
        )

        when (mealsState) {
            is ResourceState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF986ef2))
                }
            }
            is ResourceState.Success -> {
                val groupedMealsData = mealsState.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        // Передаем соответствующие списки в MealSection
                        MealSection("Завтрак", groupedMealsData.breakfast)
                        Spacer(modifier = Modifier.height(16.dp))
                        MealSection("Обед", groupedMealsData.lunch)
                        Spacer(modifier = Modifier.height(16.dp))
                        MealSection("Перекусы", groupedMealsData.snacks)
                        Spacer(modifier = Modifier.height(16.dp))
                        MealSection("Ужин", groupedMealsData.dinner)
                        Spacer(modifier = Modifier.height(24.dp))
                        DailyMealsSummary(groupedMealsData)
                    }
                }
            }
            is ResourceState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = mealsState.message,
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.loadDataForSelectedDate() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF986ef2)
                            )
                        ) {
                            Text("Попробовать снова")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CaloriesPreview() {
    MyApp()
}