package com.example.frontproject.ui.components.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontproject.MyApp
import com.example.frontproject.R
import com.example.frontproject.RmpApplication
import com.example.frontproject.data.repository.CaloriesRepository
import com.example.frontproject.domain.util.ResourceState
import com.example.frontproject.ui.components.common.ProfileHeader
import com.example.frontproject.ui.viewmodel.CaloriesTodayViewModel
import java.util.*


@Composable
fun HomeScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // header
        ProfileHeader(
            userName = "Мария Чмурова",
            profileImage = painterResource(R.drawable.profile_cat),
            navController = navController
        )
        Spacer(modifier = Modifier.height(10.dp))

        // ИМТ пользователя
        BmiCard(bmi = 35.4f) // пшеактуальное значение из ViewModel потом
        Spacer(modifier = Modifier.height(12.dp))

        // Статус активности
        Text(text = "Статус активности",
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = with(LocalDensity.current) { 16.sp },)
        Spacer(modifier = Modifier.height(12.dp))

        DashboardScreen(navController = navController)
    }
}

@Composable
fun DashboardScreen(
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CaloriesCard(
            caloriesBurned = caloriesBurned,
            caloriesLeft = 0,
            navController
        )
        /* TODO: Карточки сна/шагов */
//        StepsCard(steps = 1543, stepsGoal = 10000)
    }
}

@Composable
fun CaloriesCard(
    caloriesBurned: Int,
    caloriesLeft: Int,
    navController: NavController
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth / 2 - 25.dp
    Card(
        shape = RoundedCornerShape(25.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .size(cardWidth)
            .clickable {
                navController.navigate("calories")
            },

    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                "Калории",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )

            Text(
                text = "$caloriesBurned Ккал",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight(700),
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFe491d7),
                            Color(0xFFc254f4)
                        )
                    )
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                val progress = if (caloriesBurned == 0) {
                    0.01f
                } else {
                    caloriesBurned.toFloat() / (caloriesBurned + caloriesLeft)
                }

                CircularProgressIndicator(
                    progress = progress,
                    color = Color(0xFF986ef4),
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxSize()
                )

                // Внутренний круг с отступом 5.dp
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFc255f3),
                                    Color(0xFFe490d7)
                                ),
                                start = Offset(Float.POSITIVE_INFINITY, 0f),
                                end = Offset(0f, 0f)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${caloriesLeft}Ккал осталось",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight(500),
                            textAlign = TextAlign.Center,
                            lineHeight = 8.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BmiCard(bmi: Float) {
    val bmiStatus = when {
        bmi < 16.0f -> "Вес значительно ниже нормы."
        bmi < 18.5f -> "Вес немного ниже нормы"
        bmi < 24.9f -> "Ваш вес в норме!"
        bmi < 27.0f -> "ИМТ слегка выше нормы."
        bmi < 29.9f -> "ИМТ немного повышен."
        bmi < 34.9f -> "Вес выше нормы."
        bmi < 39.9f -> "ИМТ указывает на избыточный вес."
        else -> "ИМТ существенно выше нормы"
    }

    val titleTextSize = with(LocalDensity.current) { 16.sp }
    val statusTextSize = with(LocalDensity.current) { 14.sp }
    val buttonTextSize = with(LocalDensity.current) { 12.sp }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFc255f3),
                        Color(0xFFe490d7)
                    ),
                    start = Offset(Float.POSITIVE_INFINITY, 0f),
                    end = Offset(0f, 0f)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.70f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "ИМТ (Индекс массы тела)",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = titleTextSize,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = bmiStatus,
                    color = Color.White,
                    fontSize = statusTextSize,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { /* TODO: Подробнее */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .height(36.dp)
                ) {
                    Text(
                        "Подробнее",
                        color = Color(0xFFc255f3),
                        fontSize = buttonTextSize
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(0.30f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                CutCircle(bmi = bmi)
            }
        }
    }
}

@Composable
fun CutCircle(bmi: Float) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = minOf(canvasWidth, canvasHeight) / 2

            drawArc(
                color = Color.White,
                startAngle = 0f,
                sweepAngle = 260f,
                useCenter = true,
                topLeft = Offset(
                    (canvasWidth / 2) - radius,
                    (canvasHeight / 2) - radius
                ),
                size = Size(radius * 2, radius * 2)
            )
        }

        Text(
            text = bmi.toString(),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 10.dp)
        )
    }
}

@Preview
@Composable
fun MainPreview() {
    MyApp()
}