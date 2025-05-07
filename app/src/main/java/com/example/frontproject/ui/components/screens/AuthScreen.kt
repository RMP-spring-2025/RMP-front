package com.example.frontproject.ui.components.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    onLoginClick: () -> Unit = {},
    onPasswordRecoverClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("С возвращением,", style = MaterialTheme.typography.bodyLarge)
            Text(
                "Войти в аккаунт",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Column(
                modifier = Modifier
                    .padding(top=24.dp, bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomOutlinedTextField(value = "",
                    label = "Логин",
                    icon = Icons.Default.Person
                )
                CustomOutlinedTextField(
                    value = "",
                    label = "Пароль",
                    icon = Icons.Default.Lock,
                    isPassword = true,
                )
                Text(
                    text = "Забыли пароль?",
                    color = Color(0xFFADA4A5),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable { onPasswordRecoverClick() }
                )
            }

            GradientButton(text = "Войти", icon = Icons.AutoMirrored.Filled.ExitToApp, onClick = {onLoginClick()})

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row {
                Text("Ещё нет аккаунта?")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Зарегестрироваться",
                    color = Color(0xFF9A5AFF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    AuthScreen()
}