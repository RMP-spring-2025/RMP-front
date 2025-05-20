package com.example.frontproject.ui.components.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.*
import com.example.frontproject.RmpApplication
import com.example.frontproject.domain.util.ResourceState
import com.example.frontproject.ui.components.common.ScreenHeader
import com.example.frontproject.ui.viewmodel.fat.FatHistoryViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun FatGraphicScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ScreenHeader(
            title = "График белков",
            onBackClick = { navController.popBackStack() },
        )

        Spacer(modifier = Modifier.height(10.dp))

        FatGraphicComponent()
    }
}

@Composable
fun FatGraphicComponent(
    viewModel: FatHistoryViewModel = viewModel(
        factory = FatHistoryViewModel.provideFactory(
            (LocalContext.current.applicationContext as RmpApplication).appContainer.bzuRepository
        )
    )
) {
    val fatHistoryState = viewModel.fatHistoryState.collectAsState().value
    var selectedDays by remember { mutableStateOf(7) }

    when (fatHistoryState) {
        is ResourceState.Success -> {
            val allData = fatHistoryState.data
            val filteredData = allData.filter { pair ->
                val timestamp = pair.first
                val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                val today = LocalDate.now()
                date >= today.minusDays(selectedDays.toLong())
            }.map { it.first to it.second.toFloat() }

            if (filteredData.isEmpty()) {
                Text(
                    text = "No data available for the selected period",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Gray
                )
                return
            }

            val pointsData = filteredData.mapIndexed { index, (_, calories) ->
                Point(x = index.toFloat(), y = calories)
            }

            val dateFormatter = DateTimeFormatter.ofPattern("dd-MM")
            val xAxisData = AxisData.Builder()
                .axisStepSize(100.dp)
                .backgroundColor(Color.Transparent)
                .steps(pointsData.size - 1)
                .labelData { i ->
                    if (i >= 0 && i < filteredData.size) {
                        val timestamp = filteredData[i].first
                        Instant.ofEpochMilli(timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .format(dateFormatter)
                    } else ""
                }
                .labelAndAxisLinePadding(15.dp)
                .axisLineColor(MaterialTheme.colorScheme.tertiary)
                .axisLabelColor(MaterialTheme.colorScheme.tertiary)
                .build()

            val steps = 10
            val yAxisData = AxisData.Builder()
                .steps(steps)
                .backgroundColor(Color.Transparent)
                .labelAndAxisLinePadding(20.dp)
                .labelData { i ->
                    val yScale = (filteredData.maxOfOrNull { it.second }?.toInt() ?: 100) / steps
                    (i * yScale).toString() + "   "
                }
                .axisLineColor(MaterialTheme.colorScheme.tertiary)
                .axisLabelColor(MaterialTheme.colorScheme.tertiary)
                .build()

            val lineChartData = LineChartData(
                linePlotData = LinePlotData(
                    lines = listOf(
                        Line(
                            dataPoints = pointsData,
                            LineStyle(
                                color = Color(0xffd06ce9),
                                lineType = LineType.SmoothCurve(isDotted = false)
                            ),
                            IntersectionPoint(
                                color = Color(0xffc254f4)
                            ),
                            SelectionHighlightPoint(color = Color(0xffc254f4)),
                            ShadowUnderLine(
                                alpha = 0.5f,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.inversePrimary,
                                        Color.Transparent
                                    )
                                )
                            ),
                            SelectionHighlightPopUp(
                                popUpLabel = { x, y ->
                                    val index = x.toInt()
                                    if (index >= 0 && index < filteredData.size) {
                                        val timestamp = filteredData[index].first
                                        val date = Instant.ofEpochMilli(timestamp)
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate()
                                            .format(dateFormatter)
                                        "$date: ${y.toInt()}"
                                    } else {
                                        "?: ${y.toInt()}"
                                    }
                                }
                            )
                        )
                    )
                ),
                backgroundColor = MaterialTheme.colorScheme.surface,
                xAxisData = xAxisData,
                yAxisData = yAxisData,
                gridLines = GridLines(color = MaterialTheme.colorScheme.outlineVariant)
            )

            Column {
                // Кнопки выбора периода
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(7, 14, 30).forEach { days ->
                        Button(
                            onClick = { selectedDays = days },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedDays == days) Color(0xFFc557f4) else Color(0xffcfcfcf)
                            )
                        ) {
                            Text("$days Days")
                        }
                    }
                }

                LineChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp),
                    lineChartData = lineChartData
                )
            }
        }

        is ResourceState.Loading -> {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ResourceState.Error -> {
            Text(
                text = "Ошибка: ${fatHistoryState.message}",
                modifier = Modifier.padding(16.dp),
                color = Color.Red
            )
        }
    }
}