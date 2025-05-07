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

@Composable
fun RegistrationScreen(
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
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
            Text("Добро пожаловать,", style = MaterialTheme.typography.bodyLarge)
            Text(
                "Создать Аккаунт",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Column(
                modifier = Modifier
                    .padding(top=24.dp, bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomOutlinedTextField(value = "",
                    label = "Имя",
                    icon = Icons.Default.Person
                )
                CustomOutlinedTextField(value = "",
                    label = "Номер телефона",
                    icon = Icons.Default.Phone
                )
                CustomOutlinedTextField(
                    value = "",
                    label = "Email",
                    icon = Icons.Default.Email
                )
                CustomOutlinedTextField(
                    value = "",
                    label = "Пароль",
                    icon = Icons.Default.Lock,
                    isPassword = true,
                )
            }

            GradientButton(text = "Зарегистрироваться", onClick = {onRegisterClick()})

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

@Preview(showSystemUi = true)
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen()
}
