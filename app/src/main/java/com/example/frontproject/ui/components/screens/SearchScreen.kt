package com.example.frontproject.ui.components.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontproject.RmpApplication
import com.example.frontproject.data.model.product.Product
import com.example.frontproject.ui.viewmodel.SearchScreenUiState
import com.example.frontproject.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModel.provideFactory(
            (LocalContext.current.applicationContext as RmpApplication).appContainer.mealsRepository
        )
    )
) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by searchViewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val primaryAppColor = Color(0xff986ef2)
    val lightGrayBackgroundColor = Color(0xfff7f7f7)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGrayBackgroundColor)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Введите название продукта") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                if (searchQuery.isNotBlank()) {
                    searchViewModel.searchProductByName(searchQuery)
                    keyboardController?.hide()
                }
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryAppColor,
                focusedLabelColor = primaryAppColor,
                cursorColor = primaryAppColor,
                unfocusedBorderColor = Color.Gray,
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (searchQuery.isNotBlank()) {
                    searchViewModel.searchProductByName(searchQuery)
                    keyboardController?.hide()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = searchQuery.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryAppColor,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Найти", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (val currentState = uiState) {
            is SearchScreenUiState.Idle -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Введите название продукта для поиска.",
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray,
                        fontSize = 16.sp
                    )
                }
            }
            is SearchScreenUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryAppColor)
                }
            }
            is SearchScreenUiState.Success -> {
                if (currentState.products.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "По вашему запросу ничего не найдено.",
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(currentState.products) { product ->
                            ProductInfoCard(product = product, accentColor = primaryAppColor)
                        }
                    }
                }
            }
            is SearchScreenUiState.NoResults -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "По запросу \"${currentState.query}\" ничего не найдено.",
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray,
                        fontSize = 16.sp
                    )
                }
            }
            is SearchScreenUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Ошибка: ${currentState.message}",
                        textAlign = TextAlign.Center,
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProductInfoCard(product: Product, accentColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            ProductDetailRow(label = "Калории:", value = "${product.calories} ккал")
            ProductDetailRow(label = "Белки:", value = "${product.proteins} г")
            ProductDetailRow(label = "Жиры:", value = "${product.fats} г")
            ProductDetailRow(label = "Углеводы:", value = "${product.carbs} г")
        }
    }
}

@Composable
fun ProductDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            fontSize = 15.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            fontSize = 15.sp
        )
    }
    Spacer(modifier = Modifier.height(6.dp))
}