package com.example.frontproject.ui.components.calories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun DateSelector(
    selectedDate: LocalDate,
    currentMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit
) {
    val datesInMonth = remember(currentMonth) {
        (1..currentMonth.lengthOfMonth())
            .map { day -> currentMonth.atDay(day) }
    }

    val today = LocalDate.now()
    val listState = rememberLazyListState()

    val scrollToIndex = remember(datesInMonth, selectedDate, today) {
        when {
            datesInMonth.contains(selectedDate) -> datesInMonth.indexOf(selectedDate)
            datesInMonth.contains(today) -> datesInMonth.indexOf(today)
            else -> 0
        }
    }

    // Индикатор первого запуска (используем rememberSaveable, чтобы сохранять при перекомпозиции)
    val isFirstLoad = remember { mutableStateOf(true) }

    // Центрирование выбранной даты
    LaunchedEffect(scrollToIndex) {
        if (scrollToIndex >= 0) {
            if (isFirstLoad.value) {
                // При первом запуске скроллим без анимации
                listState.scrollToItem(scrollToIndex)
                isFirstLoad.value = false
            } else {
                // При последующих выборах используем только анимированный скролл
                val layoutInfo = listState.layoutInfo
                val viewportWidth = layoutInfo.viewportSize.width

                // Предполагаемый размер элемента (можно настроить)
                val estimatedItemSize = 68 // 60dp ширина + 8dp отступ

                // Расчет отступа для центрирования
                val centeredOffset = (viewportWidth - estimatedItemSize) / 2

                // Плавный скролл с анимацией
                listState.animateScrollToItem(
                    index = scrollToIndex,
                    scrollOffset = -centeredOffset
                )
            }
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(datesInMonth) { date ->
            val isSelected = date == selectedDate
            DateItem(
                date = date,
                isSelected = isSelected,
                isToday = date == today,
                onClick = { onDateSelected(date) }
            )
        }
    }
}


@Composable
fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = Color(0xFF986ef2)

    Card(
        modifier = Modifier
            .padding(end = 8.dp)
            .width(60.dp)
            .height(80.dp)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) primaryColor else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFEFE7FF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) primaryColor else Color.Gray
            )

            Text(
                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                fontSize = 14.sp,
                color = if (isSelected) primaryColor else Color.Gray
            )

            if (isToday) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(primaryColor)
                )
            }
        }
    }
}