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
import androidx.compose.material.icons.filled.ArrowDropDown
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

// Определим цели здесь для удобства
enum class GoalOption(val displayName: String, val apiValue: String) {
    GAIN_WEIGHT("Набрать вес", "gain weight"),
    LOOSE_WEIGHT("Сбросить вес", "loose weight"),
    KEEP_WEIGHT("Поддерживать вес", "keep weight")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    settingsViewModel: SettingsViewModel
) {
    var username by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var displayBirthDate by remember { mutableStateOf("Выберите дату рождения") }
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
                "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
            displayBirthDate =
                "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        }, year, month, day
    )
    datePickerDialog.datePicker.maxDate = Date().time

    val goalOptions = GoalOption.entries
    var expandedGoalMenu by remember { mutableStateOf(false) }
    var selectedGoal by remember { mutableStateOf(goalOptions[0]) }


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

        OutlinedTextField(
            value = displayBirthDate,
            onValueChange = { /* Блокируем прямой ввод */ },
            label = { Text("Дата рождения") },
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
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
                    .height(56.dp)
                    .width(56.dp)
                    .clip(RoundedCornerShape(4.dp)) // Стандартное скругление для OutlinedTextField
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
                    .height(56.dp)
                    .width(56.dp)
                    .clip(RoundedCornerShape(4.dp)) // Стандартное скругление для OutlinedTextField
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

        Spacer(modifier = Modifier.height(12.dp))

        // Выбор цели
        ExposedDropdownMenuBox(
            expanded = expandedGoalMenu,
            onExpandedChange = { expandedGoalMenu = !expandedGoalMenu },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedGoal.displayName,
                onValueChange = {},
                label = { Text("Ваша цель") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGoalMenu) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandedGoalMenu,
                onDismissRequest = { expandedGoalMenu = false }
            ) {
                goalOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            selectedGoal = option
                            expandedGoalMenu = false
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        GradientButton(
            text = "Дальше",
            onClick = {
                val age =
                    calculateAgeFromBirthDateString(birthDate)
                val heightValue = height.toDoubleOrNull() ?: 0.0
                val weightValue = weight.toDoubleOrNull() ?: 0.0

                settingsViewModel.createUserProfile(
                    username = username,
                    age = age,
                    height = heightValue,
                    weight = weightValue,
                    goal = selectedGoal.apiValue // Используем выбранную цель
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
        0
    }
}