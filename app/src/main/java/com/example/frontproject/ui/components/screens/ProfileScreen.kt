package com.example.frontproject.ui.components.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontproject.R
import com.example.frontproject.ui.components.common.ScreenHeader

@Composable
@Preview
fun ProfileScreen() {
    val primaryColor = Color(0xff986ef2)
    var notificationsEnabled by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
    ) {
        // Header
        ScreenHeader(
            title = "Profile",
            onBackClick = { /* TODO: Handle back click */ },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Аватарка
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.icon_profile_menu),
                        contentDescription = "Profile",
                        modifier = Modifier.size(40.dp),
                        tint = primaryColor
                    )
                }

                // Имя и программа
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Имя пользователя
                    Text(
                        text = "John Doe",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                    )

                    // Программа
                    Text(
                        text = "Weight Gain Program",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier.padding(top = 7.dp)
                    )
                }
                // Кнопка Edit
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .height(30.dp)
                        .wrapContentWidth(),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 35.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text(
                        text = "Edit",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        color = Color.White
                    )
                }
            }
            // Показатели
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp, end = 25.dp, top = 5.dp, bottom = 25.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Рост
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GradientText(text = "180 cm")
                        Text("Height", fontSize = 14.sp, color = Color(0xFFB6B4C2))
                    }
                }
                // Вес
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GradientText(text = "65kg")
                        Text("Weight", fontSize = 14.sp, color = Color(0xFFB6B4C2))
                    }
                }

                // Возраст
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GradientText(text = "20yo")
                        Text("Age", fontSize = 14.sp, color = Color(0xFFB6B4C2))
                    }
                }
            }

            // Аккаунт секция
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LinkItem("Personal Data", R.drawable.icon_profile)
                    LinkItem("Achievements", R.drawable.icon_achievement)
                    LinkItem("Activity History", R.drawable.icon_activity)
                    LinkItem("Workout Progress", R.drawable.icon_workout)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notifications секция
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Notification",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row()
                        {
                            Icon(
                                painterResource(R.drawable.icon_notif),
                                contentDescription = "Notification Icon",
                                tint = Color(0xFF00FF66),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Pop-up Notification",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W400,
                                color = Color(0xFFB6B4C2),
                            )
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                uncheckedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF00FF66).copy(alpha = 0.5f),
                                uncheckedTrackColor = Color(0xFFB6B4C2).copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Other секция
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Other",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LinkItem("Contact Us", R.drawable.icon_message)
                    LinkItem("Privacy Policy", R.drawable.icon_privacy)
                    LinkItem("Settings", R.drawable.icon_setting)
                }
            }
        }
    }
}

@Composable
fun LinkItem(title: String, icon: Int? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Действие при нажатии */ }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    painterResource(icon),
                    contentDescription = "$title Icon",
                    tint = Color(0xFF00FF66),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.W400,
                color = Color(0xFFB6B4C2),
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go to $title",
            tint = Color.Gray
        )
    }
}

@Composable
fun GradientText(
    text: String,
    fontSize: TextUnit = 18.sp,
    fontWeight: FontWeight = FontWeight.W500
) {
    val gradientColors = listOf(Color(0xFFC150F6), Color(0xFFEEA4CE))
    val brush = Brush.horizontalGradient(gradientColors)

    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        modifier = Modifier
            .graphicsLayer(alpha = 0.99f)
            .drawWithContent {
                drawContent()
                drawRect(brush = brush, blendMode = BlendMode.SrcAtop)
            }
    )
}