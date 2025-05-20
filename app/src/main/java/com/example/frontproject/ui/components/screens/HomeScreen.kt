package com.example.frontproject.ui.components.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.StrokeCap
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
import com.example.frontproject.data.repository.HealthConnectAvailability
import com.example.frontproject.domain.util.ResourceState
import com.example.frontproject.ui.components.common.ProfileHeader
import com.example.frontproject.ui.model.ProfileUiState
import com.example.frontproject.ui.viewmodel.calories.CaloriesTodayViewModel
import com.example.frontproject.ui.viewmodel.HomeViewModel
import com.example.frontproject.ui.viewmodel.StepsViewModel


@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(
            (LocalContext.current.applicationContext as RmpApplication).appContainer.userProfileRepository
        )
    )
) {
    val homeUiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current.applicationContext as RmpApplication

    val userNameToShow = when (val state = homeUiState) {
        is ProfileUiState.Success -> state.userProfile.username
        is ProfileUiState.Loading -> "Загрузка..."
        is ProfileUiState.Error -> "Пользователь"
        is ProfileUiState.Idle -> "Пользователь"
    }

    val bmiCalculated = when (val state = homeUiState) {
        is ProfileUiState.Success -> (state.userProfile.weight / (state.userProfile.height * state.userProfile.height / 10000)).toInt()
        is ProfileUiState.Loading -> 0
        is ProfileUiState.Error -> 0
        is ProfileUiState.Idle -> 0
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // header
        ProfileHeader(
            userName = userNameToShow,
            profileImage = painterResource(R.drawable.profile_cat),
            navController = navController
        )
        Spacer(modifier = Modifier.height(10.dp))

        // ИМТ пользователя
        BmiCard(bmi = bmiCalculated) // актуальное значение из ViewModel потом
        Spacer(modifier = Modifier.height(12.dp))

        // Статус активности
        Text(
            text = "Статус активности",
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = with(LocalDensity.current) { 16.sp },
        )
        Spacer(modifier = Modifier.height(12.dp))

        DashboardScreen(
            navController = navController,
            caloriesViewModel = viewModel(
                factory = CaloriesTodayViewModel.provideFactory(
                    context.appContainer.caloriesRepository
                )
            ),
            stepsViewModel = viewModel(
                factory = StepsViewModel.provideFactory(
                    context.appContainer.healthConnectRepository,
                    context
                )
            ),
            homeUiState
        )
    }
}

@Composable
fun DashboardScreen(
    navController: NavController,
    caloriesViewModel: CaloriesTodayViewModel,
    stepsViewModel: StepsViewModel,
    homeUiState: ProfileUiState
) {
    val dailyCalorieIntake =
        when (val state = homeUiState) {
            is ProfileUiState.Success -> {
                val sexConf = if (state.userProfile.sex == "Female") -161 else 5

                val goalConf = when (state.userProfile.goal) {
                    "keep weight" -> 1.0
                    "gain weight" -> 1.15
                    "loose weight" -> 0.85
                    else -> 1.0 // default case
                }

                ((10 * state.userProfile.weight +
                        6.25 * state.userProfile.height -
                        5 * state.userProfile.age +
                        sexConf) * goalConf).toInt()
            }
            is ProfileUiState.Loading -> 0
            is ProfileUiState.Error -> 0
            ProfileUiState.Idle -> 0
        }

    val caloriesBurned =
        when (val caloriesState = caloriesViewModel.caloriesState.collectAsState().value) {
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
            caloriesLeft = dailyCalorieIntake - caloriesBurned,
            navController
        )
        StepsCard(
            stepsViewModel = stepsViewModel
        )
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
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF986ef4),
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round,
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
fun StepsCard(
    stepsViewModel: StepsViewModel
) {
    val uiState by stepsViewModel.uiState.collectAsState()

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = stepsViewModel.getPermissionsLaunchIntent(),
        onResult = {
            stepsViewModel.loadStepsData()
        }
    )

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth / 2 - 25.dp

    val stepsColor1 = Color(0xFF8A6EE5)
    val stepsColor2 = Color(0xFF674ABF)

    Card(
        shape = RoundedCornerShape(25.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .size(cardWidth)
            .clickable {
                if (uiState.healthConnectAvailability == HealthConnectAvailability.AVAILABLE && !uiState.permissionsGranted) {
                    permissionsLauncher.launch(stepsViewModel.healthConnectRepository.permissions)
                } else if (uiState.healthConnectAvailability != HealthConnectAvailability.AVAILABLE) {
                    Log.w(
                        "StepsCard",
                        "Health Connect is not available: ${uiState.healthConnectAvailability}"
                    )
                } else {
                    // TODO: навигация на график шагов
                    Log.d("StepsCard", "Navigating to steps details or other action")
                }
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
                "Шаги",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = stepsColor1)
            } else if (uiState.error != null && (uiState.healthConnectAvailability != HealthConnectAvailability.AVAILABLE || !uiState.permissionsGranted)) {
                Text(
                    text = uiState.error ?: "Нет данных",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Red,
                        fontSize = 10.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
                if (uiState.healthConnectAvailability == HealthConnectAvailability.AVAILABLE) {
                    Button(
                        onClick = { permissionsLauncher.launch(stepsViewModel.healthConnectRepository.permissions) },
                        colors = ButtonDefaults.buttonColors(containerColor = stepsColor1)
                    ) {
                        Text("Дать разрешение", fontSize = 10.sp, color = Color.White)
                    }
                } else
                    Text(
                        "Health Connect недоступен",
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
            } else {
                // Отображение шагов и прогресса
                Text(
                    text = "${uiState.steps} шагов",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight(700),
                        brush = Brush.linearGradient(
                            colors = listOf(stepsColor1, stepsColor2)
                        )
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                )
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val progress =
                        if (uiState.goal == 0) 0.01f else (uiState.steps.toFloat() / uiState.goal.toFloat()).coerceIn(
                            0f,
                            1f
                        )
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = stepsColor1,
                        strokeWidth = 8.dp,
                        strokeCap = StrokeCap.Round,
                    )
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(stepsColor1, stepsColor2),
                                    start = Offset(Float.POSITIVE_INFINITY, 0f),
                                    end = Offset(0f, 0f)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Цель:\n${uiState.goal}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight(500),
                                textAlign = TextAlign.Center,
                                lineHeight = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BmiCard(bmi: Int) {
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
fun CutCircle(bmi: Int) {
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