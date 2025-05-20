package com.example.frontproject.ui.components.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontproject.R
import com.example.frontproject.RmpApplication
import com.example.frontproject.domain.util.ResourceState
import com.example.frontproject.ui.components.common.GraphicsHeader
import com.example.frontproject.ui.viewmodel.calories.CaloriesTodayViewModel
import com.example.frontproject.ui.viewmodel.calories.CarbTodayViewModel
import com.example.frontproject.ui.viewmodel.calories.FatTodayViewModel
import com.example.frontproject.ui.viewmodel.calories.ProteinTodayViewModel

@Composable
fun GraphicsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 5.dp)
    ) {
        GraphicsHeader()

        DashboardScreenForGraphics(navController = navController)
    }
}


@Composable
fun DashboardScreenForGraphics(
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { GraphicCaloriesCard(navController) }
        item { GraphicProteinsCard(navController) }
        item { GraphicFatsCard(navController) }
        item { GraphicСarbohydratesCard(navController) }

        // Добавляем дополнительный Spacer внизу
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun GraphicСarbohydratesCard(
    navController: NavController,
    viewModel: CarbTodayViewModel = viewModel(
        factory = CarbTodayViewModel.provideFactory(
            (LocalContext.current.applicationContext as RmpApplication).appContainer.bzuRepository
        )
    )
) {
    val carbsBurned = when (val caloriesState = viewModel.carbState.collectAsState().value) {
        is ResourceState.Success -> caloriesState.data
        is ResourceState.Loading -> 0
        is ResourceState.Error -> 0
    }
    val purple = Color(0xFFc85fee)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFf3ddfd),
                        Color(0xFFf9e8f7)
                    ),
                    start = Offset(Float.POSITIVE_INFINITY, 0f),
                    end = Offset(0f, 0f)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { navController.navigate("carbs_chart_screen") }
    ) {
        Row( // Изменяем Column на Row для горизонтального расположения
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f) // Текст занимает доступное пространство
            ) {
                Text(
                    text = "Ваши углеводы",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$carbsBurned грамм",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight(700),
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFe490d7),
                                Color(0xFFc557f3)
                            )
                        )
                    )
                )
            }

            // Добавляем изображение справа
            Image(
                painter = painterResource(id = R.drawable.u),
                contentDescription = "Proteins icon",
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun GraphicFatsCard(
    navController: NavController,
    viewModel: FatTodayViewModel = viewModel(
        factory = FatTodayViewModel.provideFactory(
            (LocalContext.current.applicationContext as RmpApplication).appContainer.bzuRepository
        )
    )
) {
    val fatsBurned = when (val caloriesState = viewModel.fatState.collectAsState().value) {
        is ResourceState.Success -> caloriesState.data
        is ResourceState.Loading -> 0
        is ResourceState.Error -> 0
    }

    val purple = Color(0xFFc85fee)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFf3ddfd),
                        Color(0xFFf9e8f7)
                    ),
                    start = Offset(Float.POSITIVE_INFINITY, 0f),
                    end = Offset(0f, 0f)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { navController.navigate("fats_chart_screen") }
    ) {
        Row( // Изменяем Column на Row для горизонтального расположения
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f) // Текст занимает доступное пространство
            ) {
                Text(
                    text = "Ваши жиры",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$fatsBurned грамм",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight(700),
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFe490d7),
                                Color(0xFFc557f3)
                            )
                        )
                    )
                )
            }

            // Добавляем изображение справа
            Image(
                painter = painterResource(id = R.drawable.z),
                contentDescription = "Proteins icon",
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun GraphicProteinsCard(
    navController: NavController,
    viewModel: ProteinTodayViewModel = viewModel(
        factory = ProteinTodayViewModel.provideFactory(
            (LocalContext.current.applicationContext as RmpApplication).appContainer.bzuRepository
        )
    )
) {
    val proteinsBurned = when (val caloriesState = viewModel.proteinState.collectAsState().value) {
        is ResourceState.Success -> caloriesState.data
        is ResourceState.Loading -> 0
        is ResourceState.Error -> 0
    }

    val purple = Color(0xFFc85fee)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFf3ddfd),
                        Color(0xFFf9e8f7)
                    ),
                    start = Offset(Float.POSITIVE_INFINITY, 0f),
                    end = Offset(0f, 0f)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { navController.navigate("proteins_chart_screen") }
    ) {
        Row( // Изменяем Column на Row для горизонтального расположения
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f) // Текст занимает доступное пространство
            ) {
                Text(
                    text = "Ваши белки",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$proteinsBurned грамм",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight(700),
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFe490d7),
                                Color(0xFFc557f3)
                            )
                        )
                    )
                )
            }

            // Добавляем изображение справа
            Image(
                painter = painterResource(id = R.drawable.b),
                contentDescription = "Proteins icon",
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Fit
            )
        }
    }
}


@Composable
fun GraphicCaloriesCard(
    navController: NavController,
    viewModel: CaloriesTodayViewModel = viewModel(
        factory = CaloriesTodayViewModel.provideFactory(
            (LocalContext.current.applicationContext as RmpApplication).appContainer.caloriesRepository
        )
    )
) {
    val caloriesBurned = when (val caloriesState = viewModel.caloriesState.collectAsState().value) {
        is ResourceState.Success -> caloriesState.data
        is ResourceState.Loading -> 0
        is ResourceState.Error -> 0
    }

    val purple = Color(0xFFc85fee)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFf3ddfd),
                        Color(0xFFf9e8f7)
                    ),
                    start = Offset(Float.POSITIVE_INFINITY, 0f),
                    end = Offset(0f, 0f)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { navController.navigate("calories_chart_screen") }
    ) {
        Column() {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Ваши калории",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$caloriesBurned Ккал",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight(700),
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFe490d7),
                                Color(0xFFc557f3)
                            )
                        )
                    )
                )
            }

            StaticLineChart(color = purple)
        }
    }
}

@Composable
fun StaticLineChart(
    color: Color,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
) {
    Canvas(modifier = modifier) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, size.height * 0.5f)
            lineTo(size.width * 0.1f, size.height * 0.2f)
            lineTo(size.width * 0.2f, size.height * 0.8f)
            lineTo(size.width * 0.3f, size.height * 0.1f)
            lineTo(size.width * 0.4f, size.height * 0.7f)
            lineTo(size.width * 0.5f, size.height * 0.3f)
            lineTo(size.width * 0.6f, size.height * 0.9f)
            lineTo(size.width * 0.7f, size.height * 0.4f)
            lineTo(size.width * 0.8f, size.height * 0.6f)
            lineTo(size.width * 0.9f, size.height * 0.2f)
            lineTo(size.width, size.height * 0.5f)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}


