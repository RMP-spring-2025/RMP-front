package com.example.frontproject.ui.components.calories

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Month
import java.time.YearMonth

@Composable
fun MonthSelector(
    currentMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onMonthChanged(currentMonth.minusMonths(1)) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Предыдущий месяц",
                tint = Color(0xFF986ef2)
            )
        }

        Text(
            text = "${getMonthName(currentMonth.month)} ${currentMonth.year}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )

        IconButton(onClick = { onMonthChanged(currentMonth.plusMonths(1)) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Следующий месяц",
                tint = Color(0xFF986ef2)
            )
        }
    }
}

// Функция для получения названия месяца на русском
fun getMonthName(month: Month): String {
    val monthNames = mapOf(
        Month.JANUARY to "Январь",
        Month.FEBRUARY to "Февраль",
        Month.MARCH to "Март",
        Month.APRIL to "Апрель",
        Month.MAY to "Май",
        Month.JUNE to "Июнь",
        Month.JULY to "Июль",
        Month.AUGUST to "Август",
        Month.SEPTEMBER to "Сентябрь",
        Month.OCTOBER to "Октябрь",
        Month.NOVEMBER to "Ноябрь",
        Month.DECEMBER to "Декабрь"
    )
    return monthNames[month] ?: ""
}