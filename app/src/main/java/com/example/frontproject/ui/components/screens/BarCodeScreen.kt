package com.example.frontproject.ui.components.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontproject.RmpApplication
import com.example.frontproject.ui.model.BarcodeUiState
import com.example.frontproject.data.model.product.Product // Убедитесь, что импорт правильный
import com.example.frontproject.domain.util.ResourceState
import com.example.frontproject.ui.components.barcode.AddProductDialog
import com.example.frontproject.ui.components.barcode.ProductAddedView
import com.example.frontproject.ui.components.barcode.ProductFoundView
import com.example.frontproject.ui.components.barcode.ProductNotFoundView
import com.example.frontproject.ui.viewmodel.BarCodeViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

// Глобальная переменная для последнего значения штрих-кода, если она все еще нужна где-то
var lastBarcodeValue: String? = null

@Composable
fun BarCodeScreen(
    navController: NavController,
    viewModel: BarCodeViewModel = viewModel(
        factory = BarCodeViewModel.provideFactory(
            (LocalContext.current.applicationContext as RmpApplication).appContainer.mealsRepository
        )
    )
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val uiState by viewModel.uiState.collectAsState() // Renamed to uiState for clarity

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showAddProductDialog by remember { mutableStateOf(false) }
    var showSearchByNameDialog by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (val currentState = uiState) { // Use the collected uiState
            is BarcodeUiState.Loading, BarcodeUiState.SearchingByName -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is BarcodeUiState.ProductFound -> {
                when (val productState = currentState.product) {
                    is ResourceState.Success -> {
                        ProductFoundView(
                            product = productState.data,
                            onAddProduct = { mass, time -> // Изменено onConsume на onAddProduct
                                // Убедимся, что mass это Float для viewModel
                                val massFloat = mass.toFloatOrNull() ?: 0f
                                viewModel.addProductWithMass(productState.data, massFloat, time)
                            }
                            // onScanAgain удален, так как его нет в ProductFoundView
                        )
                    }

                    is ResourceState.Error -> {
                        // Показываем ошибку, если ResourceState.Error
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Ошибка загрузки продукта: ${productState.message}",
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.resetState() }) { Text("Сканировать снова") }
                        }
                    }

                    is ResourceState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            is BarcodeUiState.ProductsFoundByName -> {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)) {
                    Text(
                        "Найденные продукты:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(currentState.products) { product ->
                            ProductRow(product = product, onClick = {
                                viewModel.manualProductSelect(product)
                                showSearchByNameDialog = false
                            })
                        }
                    }
                    Button(
                        onClick = { viewModel.resetState() },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp)
                    ) {
                        Text("Сканировать штрих-код")
                    }
                }
            }

            is BarcodeUiState.ProductNotFound -> {
                ProductNotFoundView(
                    onManualAdd = {
                        showAddProductDialog = true

                    },
                    onScanAgain = { viewModel.resetState() }
                )
            }

            is BarcodeUiState.NoProductsFoundByName -> {
                NoProductsFoundByNameView(
                    query = currentState.query,
                    onSearchAgain = {
                        showSearchByNameDialog = true // Снова открываем диалог поиска
                    },
                    onScan = { viewModel.resetState() } // Переходим к сканированию
                )
            }

            is BarcodeUiState.ProductAdded -> {
                ProductAddedView(
                    onDone = {
                        viewModel.resetState() // Сбрасываем состояние после добавления продукта
                        navController.navigate("home") // Переход на главный экран или другой экран
                    }
                )
            }

            is BarcodeUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Произошла ошибка: ${currentState.message}",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.resetState() }) { Text("Попробовать снова") }
                }
            }

            BarcodeUiState.Scanning -> {
                if (hasCameraPermission) {
                    AndroidView(
                        factory = { context ->
                            val previewView = PreviewView(context)
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .apply {
                                    setAnalyzer(
                                        Executors.newSingleThreadExecutor(),
                                        BarcodeAnalyzer { barcode ->
                                            val currentAnalyzerState = uiState
                                            if (barcode != lastBarcodeValue || (currentAnalyzerState is BarcodeUiState.Scanning)) {
                                                lastBarcodeValue = barcode
                                                viewModel.checkProduct(barcode)
                                            }
                                        }
                                    )
                                }
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                Log.e("BarCodeScreen", "Use case binding failed", e)
                            }
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Требуется разрешение на использование камеры")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { requestPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                                Text("Предоставить разрешение")
                            }
                        }
                    }
                }
            }
        }

        // Кнопка "Найти по названию" внизу экрана
        if (uiState is BarcodeUiState.Scanning || uiState is BarcodeUiState.Error || uiState is BarcodeUiState.ProductNotFound || uiState is BarcodeUiState.NoProductsFoundByName) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp), // Отступ снизу
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = { showSearchByNameDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Ввести вручную",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ввести вручную", color = Color.White)
                }
            }
        }
    }

    if (showAddProductDialog) {
        val barcodeForDialog =
            (uiState as? BarcodeUiState.ProductNotFound)?.identifier ?: lastBarcodeValue ?: ""
        AddProductDialog(
            barcodeValue = barcodeForDialog,
            onDismiss = { showAddProductDialog = false },
            onProductAdd = { product ->
                viewModel.addNewProduct(product)
                showAddProductDialog = false
            }
        )
    }

    if (showSearchByNameDialog) {
        SearchByNameDialog(
            onDismiss = {
                showSearchByNameDialog = false
                if (uiState is BarcodeUiState.Error || uiState is BarcodeUiState.NoProductsFoundByName) {
                    viewModel.resetState()
                }
            },
            onSearch = { name ->
                viewModel.searchProductByName(name)
                showSearchByNameDialog = false // Закрываем диалог после поиска
            }
        )
    }
}

@Composable
fun SearchByNameDialog(
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Поиск продукта по названию",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Название продукта") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (productName.isNotBlank()) {
                            onSearch(productName)
                        }
                    }) {
                        Text("Найти")
                    }
                }
            }
        }
    }
}

@Composable
fun ProductRow(product: Product, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(text = "${product.calories} ккал", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun NoProductsFoundByNameView(query: String, onSearchAgain: () -> Unit, onScan: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "По запросу \"$query\" ничего не найдено.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onSearchAgain, modifier = Modifier.fillMaxWidth()) {
            Text("Искать снова по названию")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onScan, modifier = Modifier.fillMaxWidth()) {
            Text("Сканировать штрих-код")
        }
    }
}


private class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
    )
    private var lastAnalyzedTimestamp = 0L

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAnalyzedTimestamp < 1000) { // Анализ не чаще раза в секунду
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.rawValue?.let { barcode ->
                        onBarcodeDetected(barcode)
                        lastAnalyzedTimestamp =
                            currentTime // Обновляем время только после успешного обнаружения
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("BarcodeAnalyzer", "Barcode scanning failed", exception)
                }
                .addOnCompleteListener {
                    imageProxy.close() // Закрываем imageProxy в любом случае
                }
        } else {
            imageProxy.close()
        }
    }
}