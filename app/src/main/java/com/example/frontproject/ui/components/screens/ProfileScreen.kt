package com.example.frontproject.ui.components.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ProfileScreen() {
    val primaryColor = Color(0xff986ef2)
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        TopAppBar(
            title = { Text("Profile", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally)) },
            navigationIcon = {
                IconButton(onClick = { /* Навигация назад */ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
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
                    painterResource(R.drawable.icon_profile),
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Вес
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("75 kg", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Weight", fontSize = 14.sp, color = Color.Gray)
                }

                // Возраст
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("28 y.o.", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Age", fontSize = 14.sp, color = Color.Gray)
                }

                // Рост
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("180 cm", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Height", fontSize = 14.sp, color = Color.Gray)
                }
            }
        }

        // Account секция
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                AccountItem("Personal Data")
                AccountItem("Achievements")
                AccountItem("Activity History")
                AccountItem("Workout Progress")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications секция
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Notifications",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Notifications", fontSize = 16.sp)
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = primaryColor
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun AccountItem(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Действие при нажатии */ }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp)
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go to $title",
            tint = Color.Gray
        )
    }
}