package com.example.frontproject.ui.components.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.frontproject.ui.viewmodel.RegisterState
import com.example.frontproject.ui.viewmodel.SettingsViewModel

@Composable
fun RegistrationScreen(
    settingsViewModel: SettingsViewModel,
    onLoginClick: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val registerState by settingsViewModel.registerState.collectAsState()

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
            Text("Добро пожаловать,", style = MaterialTheme.typography.bodyLarge)
            Text(
                "Создать Аккаунт",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Column(
                modifier = Modifier
                    .padding(top=24.dp, bottom = 160.dp),
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
            }

            GradientButton(
                text = "Зарегистрироваться",
                onClick = { settingsViewModel.registerUser(username, password) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row {
                Text("Уже есть аккаунт?")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Войти",
                    color = Color(0xFF9A5AFF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }

            if (registerState is RegisterState.Error) {
                Text(
                    text = (registerState as RegisterState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    var textValue by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = textValue,
        onValueChange = {textValue = it},
        label = { Text(label) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        trailingIcon = {
            if (isPassword) {
                val visibilityIcon = if (passwordVisible) Icons.Default.Edit else Icons.Default.Close
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = visibilityIcon, contentDescription = null)
                }
            }
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(15.dp),
        singleLine = true
    )
}
