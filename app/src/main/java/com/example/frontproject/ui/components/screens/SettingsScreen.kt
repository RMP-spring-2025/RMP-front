package com.example.frontproject.ui.components.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontproject.RmpApplication
import com.example.frontproject.ui.components.common.ScreenHeader
import com.example.frontproject.ui.viewmodel.AuthState
import com.example.frontproject.ui.viewmodel.SaveState
import com.example.frontproject.ui.viewmodel.SettingsViewModel
import com.example.frontproject.ui.viewmodel.SettingsViewModelFactory

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val appContainer = (context.applicationContext as RmpApplication).appContainer
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            settingsRepository = appContainer.settingsRepository,
            tokenRepository = appContainer.tokenRepository,
            apiRequestExecutor = appContainer.apiRequestExecutor
        )
    )

    val currentBaseUrl by settingsViewModel.currentBaseUrl.collectAsState()
    var textFieldValue by remember(currentBaseUrl) { mutableStateOf(TextFieldValue(currentBaseUrl)) }
    val saveState by settingsViewModel.saveState.collectAsState()
    val authState by settingsViewModel.authState.collectAsState()

    // Обработка сохранения URL
    LaunchedEffect(saveState) {
        if (saveState is SaveState.Success) {
            Toast.makeText(
                context,
                "URL сохранен. Перезапустите приложение для применения.",
                Toast.LENGTH_LONG
            ).show()
            settingsViewModel.resetSaveState()
        }
    }

    // Обработка состояния авторизации
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                Toast.makeText(context, "Авторизация успешна!", Toast.LENGTH_SHORT).show()
                settingsViewModel.resetAuthState()
            }

            is AuthState.Error -> {
                Toast.makeText(context, "Ошибка авторизации: ${state.message}", Toast.LENGTH_LONG)
                    .show()
                settingsViewModel.resetAuthState()
            }

            is AuthState.LogoutSuccess -> {
                Toast.makeText(context, "Выход успешен!", Toast.LENGTH_SHORT).show()
                settingsViewModel.resetAuthState()
            }

            is AuthState.Loading -> {
            }

            is AuthState.Idle -> {
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
    ) {
        ScreenHeader(
            title = "Настройки",
            onBackClick = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Настройка базового URL сервера",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    settingsViewModel.onNewUrlChange(it.text)
                },
                label = { Text("Базовый URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    settingsViewModel.saveBaseUrl()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить URL")
            }

            Text(
                text = "Текущий URL: $currentBaseUrl",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Text(
                text = "Внимание: После изменения URL необходимо перезапустить приложение, чтобы изменения вступили в силу.",
                fontSize = 12.sp,
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Авторизация",
                style = MaterialTheme.typography.titleMedium
            )

            // Кнопка авторизации
            Button(
                onClick = {
                    settingsViewModel.login("string", "string")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Авторизоваться (string/string)")
                }
            }
            // Кнопка выхода
            Button(
                onClick = {
                    settingsViewModel.logout()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Выйти")
                }
            }
        }
    }
}