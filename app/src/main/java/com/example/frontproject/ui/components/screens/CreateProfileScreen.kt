package com.example.frontproject.ui.components.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.frontproject.R
import com.example.frontproject.ui.viewmodel.CreateUserState
import com.example.frontproject.ui.viewmodel.SettingsViewModel
import java.util.Calendar
import java.util.Date

@Composable
fun CreateProfileScreen(
    settingsViewModel: SettingsViewModel
) {
    var username by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") } // Будет хранить выбранную дату в формате ГГГГ-ММ-ДД
    var displayBirthDate by remember { mutableStateOf("Выберите дату рождения") } // Для отображения
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    val createUserState by settingsViewModel.createUserState.collectAsState()

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // State для DatePickerDialog
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            birthDate =
                "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth" // Сохраняем в формате для API
            displayBirthDate =
                "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear" // Формат для отображения
        }, year, month, day
    )
    // Ограничиваем максимальную дату сегодняшним днем
    datePickerDialog.datePicker.maxDate = Date().time


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.create_profile),
            contentDescription = "Profile Illustration",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Давайте заполним ваш профиль",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Имя пользователя") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Поле для выбора даты рождения
        OutlinedTextField(
            value = displayBirthDate,
            onValueChange = { /* Блокируем прямой ввод */ },
            label = { Text("Дата рождения") },
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },
            readOnly = true, // Делаем поле только для чтения
            enabled = false, // Отключаем, чтобы цвет был как у неактивного поля, но clickable работает
            colors = OutlinedTextFieldDefaults.colors(
                // Кастомные цвета для "отключенного" состояния
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )


        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = weight,
                onValueChange = { if (it.all { char -> char.isDigit() }) weight = it },
                label = { Text("Ваш вес") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .height(56.dp) // Стандартная высота OutlinedTextField
                    .width(56.dp)
                    .clip(RoundedCornerShape(15.dp)) // Скругление углов
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF9DCEFF), Color(0xFF92A3FD))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("КГ", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = height,
                onValueChange = { if (it.all { char -> char.isDigit() }) height = it },
                label = { Text("Ваш рост") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .height(56.dp) // Стандартная высота OutlinedTextField
                    .width(56.dp)
                    .clip(RoundedCornerShape(15.dp)) // Скругление углов
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF9DCEFF), Color(0xFF92A3FD))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("СМ", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GradientButton(
            text = "Дальше",
            onClick = {
                val age =
                    calculateAgeFromBirthDateString(birthDate) // Используем birthDate (ГГГГ-ММ-ДД)
                val heightValue = height.toDoubleOrNull() ?: 0.0
                val weightValue = weight.toDoubleOrNull() ?: 0.0 // Вес не передается в createUserProfile

                settingsViewModel.createUserProfile(
                    username = username,
                    age = age,
                    height = heightValue,
                    weight = weightValue,
                    goal = "gain weight"
                )
            }
        )

        if (createUserState is CreateUserState.Error) {
            Text(
                text = (createUserState as CreateUserState.Error).message,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

// Вспомогательная функция для расчета возраста из строки ГГГГ-ММ-ДД
fun calculateAgeFromBirthDateString(birthDateString: String): Int {
    if (birthDateString.isBlank() || !birthDateString.contains("-")) return 0
    return try {
        val parts = birthDateString.split("-")
        val birthYear = parts[0].toInt()
        val birthMonth = parts[1].toInt()
        val birthDay = parts[2].toInt()

        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthYear
        if (today.get(Calendar.MONTH) < birthMonth - 1 ||
            (today.get(Calendar.MONTH) == birthMonth - 1 && today.get(Calendar.DAY_OF_MONTH) < birthDay)
        ) {
            age--
        }
        age
    } catch (e: Exception) {
        0 // Возвращаем 0 в случае ошибки парсинга
    }
}