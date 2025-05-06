package com.example.frontproject.ui.components.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontproject.RmpApplication
import com.example.frontproject.ui.components.common.ScreenHeader
import com.example.frontproject.ui.viewmodel.AuthState
import com.example.frontproject.ui.viewmodel.CreateUserState
import com.example.frontproject.ui.viewmodel.RegisterState
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
    val registerState by settingsViewModel.registerState.collectAsState()
    val createUserState by settingsViewModel.createUserState.collectAsState()

    var loginUsername by remember { mutableStateOf(TextFieldValue("")) }
    var loginPassword by remember { mutableStateOf(TextFieldValue("")) }
    var registerUsername by remember { mutableStateOf(TextFieldValue("")) }
    var registerPassword by remember { mutableStateOf(TextFieldValue("")) }

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

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is RegisterState.Success -> {
                Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                settingsViewModel.resetRegisterState()
            }

            is RegisterState.Error -> {
                Toast.makeText(context, "Ошибка регистрации: ${state.message}", Toast.LENGTH_LONG)
                    .show()
                settingsViewModel.resetRegisterState()
            }

            else -> Unit
        }
    }

    LaunchedEffect(createUserState) {
        when (val state = createUserState) {
            is CreateUserState.Success -> {
                Toast.makeText(context, "Пользователь создан!", Toast.LENGTH_SHORT).show()
                settingsViewModel.resetCreateUserState()
            }

            is CreateUserState.Error -> {
                Toast.makeText(
                    context,
                    "Ошибка создания пользователя: ${state.message}",
                    Toast.LENGTH_LONG
                )
                    .show()
                settingsViewModel.resetCreateUserState()
            }

            else -> Unit
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
                .verticalScroll(rememberScrollState()) // Добавлено для прокрутки
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
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = registerUsername,
                onValueChange = { registerUsername = it },
                label = { Text("Имя пользователя для регистрации") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = registerPassword,
                onValueChange = { registerPassword = it },
                label = { Text("Пароль для регистрации") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = {
                    settingsViewModel.registerUser(registerUsername.text, registerPassword.text)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = registerState !is RegisterState.Loading && authState !is AuthState.Loading
            ) {
                if (registerState is RegisterState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Зарегистрироваться")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Авторизация",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = loginUsername,
                onValueChange = { loginUsername = it },
                label = { Text("Имя пользователя для входа") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = loginPassword,
                onValueChange = { loginPassword = it },
                label = { Text("Пароль для входа") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = {
                    settingsViewModel.login(loginUsername.text, loginPassword.text)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is AuthState.Loading && registerState !is RegisterState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Авторизоваться")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Создание профиля пользователя (тест)",
                style = MaterialTheme.typography.titleMedium
            )
            Button(
                onClick = {
                    settingsViewModel.createUserProfile("string", 21, 185.5)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = createUserState !is CreateUserState.Loading
            ) {
                if (createUserState is CreateUserState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Создать пользователя (тест)")
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
                Text("Выйти")
            }
        }
    }
}