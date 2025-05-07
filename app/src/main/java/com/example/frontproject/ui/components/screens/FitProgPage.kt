package com.example.frontproject.ui.components.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Preview
@Composable
fun FitProgPage() {
    // Основной фоновый цвет
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Заголовок страницы
            Text(
                text = "Какова ваша цель?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.dp)
            )

            // Подзаголовок
            Text(
                text = "Это поможет нам подобрать лучшую программу для вас",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            // LazyColumn для прокрутки фиолетовых блоков
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Занимает оставшееся пространство
                    .padding(top = 32.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(3) { index ->
                    // Фиолетовый блок
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(280.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEC99FF)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Иллюстрация человека (замените на ваш SVG или PNG)
//                            Text(
//                                contentDescription = "Иллюстрация человека",
//                                modifier = Modifier.align(Alignment.Center)
//                            )

                            // Текст "Улучшить форму"
                            Text(
                                text = "Улучшить форму",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 16.dp)
                            )

                            // Дополнительный текст
                            Text(
                                text = "У меня низкий уровень жира в организме и мне нужно/хочу нарастить больше мышечной массы",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 32.dp)
                            )
                        }
                    }
                }
            }

            // Кнопка "Подтвердить"
            Button(
                onClick = {  },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF8000FF), Color(0xFFCE9FFC))
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
            ) {
                Text(
                    text = "Регистрация",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
    }
}