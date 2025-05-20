package com.example.frontproject.ui.components.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.frontproject.ui.components.common.GraphicsHeader
import kotlin.io.path.Path

@Composable
fun GraphicsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        GraphicsHeader()
        Spacer(modifier = Modifier.height(10.dp))

        DashboardScreenForGraphics(navController = navController)
    }
}


@Composable
fun DashboardScreenForGraphics(
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GraphicCaloriesCard(navController)

    }
}

@Composable
fun GraphicCaloriesCard(
    navController: NavController,
) {
    val purple = Color(0xFFc85fee)
    val bgColor = Color(0xFFf6dffb)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .clickable {
                navController.navigate("calories_chart_screen")
            }
    ) {
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
                text = "1450 Ккал",
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


