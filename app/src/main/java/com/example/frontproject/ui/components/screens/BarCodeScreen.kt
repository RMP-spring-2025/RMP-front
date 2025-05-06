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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontproject.RmpApplication
import com.example.frontproject.data.model.BarcodeUiState
import com.example.frontproject.data.model.Product
import com.example.frontproject.domain.util.ResourceState
import com.example.frontproject.ui.viewmodel.BarCodeViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

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
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val state by viewModel.uiState.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showAddProductDialog by remember { mutableStateOf(false) }

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
        when (val currentState = state) {
            is BarcodeUiState.Loading -> {
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
                        val product = productState.data
                        ProductFoundView(product) { mass, time ->
                            viewModel.addProductWithMass(product, mass.toFloatOrNull() ?: 0f,
                                time)
                        }
                    }

                    is ResourceState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Ошибка: ${productState.message}", color = Color.Red)
                        }
                    }

                    is ResourceState.Loading -> {
                        // Отображение индикатора загрузки
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            is BarcodeUiState.ProductNotFound -> {
                ProductNotFoundView(
                    onManualAdd = { showAddProductDialog = true },
                    onScanAgain = { viewModel.resetState() }
                )
            }

            is BarcodeUiState.ProductAdded -> {
                ProductAddedView(onDone = { navController.popBackStack() })
            }

            is BarcodeUiState.Error -> { // Обработка общей ошибки из BarcodeUiState
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка: ${currentState.message}", color = Color.Red)
                }
            }

            else -> {
                // Основной вид сканера
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Заголовок сканера
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xff986ef2))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Наведите камеру на штрих-код",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Отображение камеры и сканера
                    if (hasCameraPermission) {
                        Box(modifier = Modifier.weight(1f)) {
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = { context ->
                                    val previewView = PreviewView(context)
                                    val cameraExecutor = Executors.newSingleThreadExecutor()
                                    val cameraProviderFuture =
                                        ProcessCameraProvider.getInstance(context)

                                    cameraProviderFuture.addListener({
                                        val cameraProvider = cameraProviderFuture.get()

                                        val preview = Preview.Builder().build().also {
                                            it.surfaceProvider = previewView.surfaceProvider
                                        }

                                        val imageAnalysis = ImageAnalysis.Builder()
                                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                            .build()

                                        val options = BarcodeScannerOptions.Builder()
                                            .setBarcodeFormats(
                                                Barcode.FORMAT_EAN_13,
                                                Barcode.FORMAT_EAN_8,
                                                Barcode.FORMAT_UPC_A,
                                                Barcode.FORMAT_UPC_E,
                                                Barcode.FORMAT_CODE_39,
                                                Barcode.FORMAT_CODE_93,
                                                Barcode.FORMAT_CODE_128,
                                                Barcode.FORMAT_QR_CODE
                                            )
                                            .build()

                                        val barcodeScanner = BarcodeScanning.getClient(options)

                                        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                            processImageProxy(
                                                imageProxy,
                                                barcodeScanner
                                            ) { barcodeValue ->
                                                viewModel.checkProduct(barcodeValue)
                                            }
                                        }

                                        try {
                                            cameraProvider.unbindAll()
                                            cameraProvider.bindToLifecycle(
                                                lifecycleOwner,
                                                CameraSelector.DEFAULT_BACK_CAMERA,
                                                preview,
                                                imageAnalysis
                                            )
                                        } catch (e: Exception) {
                                            Log.e("BarCodeScreen", "Ошибка при привязке камеры", e)
                                        }
                                    }, ContextCompat.getMainExecutor(context))

                                    previewView
                                }
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Необходимо разрешение на использование камеры",
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        // Кнопка "Ввести вручную" внизу экрана
        // Отображаем кнопку только если не идет процесс добавления или продукт не найден
        if (state !is BarcodeUiState.ProductAdded && state !is BarcodeUiState.ProductFound) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Button(
                    onClick = { showAddProductDialog = true },
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 200.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff986ef2)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Ввести вручную",
                            tint = Color.White
                        )
                        Text(
                            text = "Ввести вручную",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // Диалог добавления нового продукта
    if (showAddProductDialog) {
        AddProductDialog(
            barcodeValue = (state as? BarcodeUiState.ProductNotFound)?.barcode ?: "",
            onDismiss = { showAddProductDialog = false },
            onProductAdd = { product ->
                viewModel.addNewProduct(product)
                showAddProductDialog = false
            }
        )
    }
}


// Вид для найденного продукта
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

// Вид для ненайденного продукта
@Composable
fun ProductNotFoundView(onManualAdd: () -> Unit, onScanAgain: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = Color(0xff986ef2),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Продукт не найден",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onManualAdd,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff986ef2)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить новый продукт")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onScanAgain,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сканировать снова")
        }
    }
}

// Диалог добавления нового продукта
@Composable
fun AddProductDialog(
    barcodeValue: String,
    onDismiss: () -> Unit,
    onProductAdd: (Product) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf(barcodeValue) }
    var proteins by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var mass by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавление нового продукта") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Штрих-код (опционально)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название продукта*") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Калории*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = proteins,
                        onValueChange = { proteins = it },
                        label = { Text("Белки (B)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    )

                    OutlinedTextField(
                        value = fats,
                        onValueChange = { fats = it },
                        label = { Text("Жиры (Ж)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it },
                    label = { Text("Углеводы (У)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mass,
                    onValueChange = { mass = it },
                    label = { Text("Масса (г, опционально)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val product = Product(
                        id = 0, // ID будет присвоен на сервере
                        name = name,
                        barcode = barcode.toLongOrNull()
                            ?: 0L, // 0L если штрих-код невалидный или пустой
                        calories = calories.toIntOrNull() ?: 0,
                        proteins = proteins.toFloatOrNull() ?: 0f,
                        fats = fats.toFloatOrNull() ?: 0f,
                        carbs = carbs.toFloatOrNull() ?: 0f,
                        mass = mass.toIntOrNull() // mass может быть null
                    )
                    onProductAdd(product)
                },
                enabled = name.isNotBlank() && calories.isNotBlank()
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

// Вид для успешно добавленного продукта
@Composable
fun ProductAddedView(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Продукт добавлен",
            tint = Color(0xff986ef2),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Продукт успешно добавлен!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onDone,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff986ef2)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Готово")
        }
    }
}

private var barcodeValueJob: Job? = null
var lastBarcodeValue: String? = null

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    barcodeScanner: BarcodeScanner,
    onBarcodeDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    barcodes.firstOrNull()?.rawValue?.let { barcodeValue ->
                        // Debounce logic
                        if (barcodeValue != lastBarcodeValue) {
                            lastBarcodeValue = barcodeValue
                            barcodeValueJob?.cancel()
                            barcodeValueJob = CoroutineScope(Dispatchers.Main).launch {
                                delay(500) // Задержка в 500 миллисекунд
                                onBarcodeDetected(barcodeValue)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e("BarCodeScreen", "Ошибка сканирования штрих-кода", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}