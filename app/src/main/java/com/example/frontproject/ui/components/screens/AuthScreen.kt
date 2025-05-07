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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontproject.ui.viewmodel.AuthState
import com.example.frontproject.ui.viewmodel.SettingsViewModel

@Composable
fun AuthScreen(
    settingsViewModel: SettingsViewModel,
    onRegisterClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by settingsViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            settingsViewModel.resetAuthState()
            onLoginSuccess()
        }
    }

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
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Логин") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Забыли пароль?",
                    color = Color(0xFFADA4A5),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable { /* onPasswordRecoverClick() */ }
                )
            }

            GradientButton(
                text = "Войти",
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                onClick = { settingsViewModel.login(username, password) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row {
                Text("Ещё нет аккаунта?")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Зарегистрироваться",
                    color = Color(0xFF9A5AFF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }

            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}