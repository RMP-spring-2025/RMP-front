package com.example.frontproject.ui.components.barcode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontproject.data.model.Product
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ProductFoundView(product: Product, onAddProduct: (String, String) -> Unit) {
    var mass by remember { mutableStateOf("") }
    val defaultTime = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
    var time by remember { mutableStateOf(defaultTime) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = product.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xff986ef2),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = mass,
            onValueChange = { mass = it },
            label = { Text("Масса (г)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Время (yyyy-MM-dd'T'HH:mm:ss)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onAddProduct(mass, time) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff986ef2)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить")
        }
    }
}