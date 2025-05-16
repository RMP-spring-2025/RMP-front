package com.example.frontproject.data.repository

import android.util.Log
import com.example.frontproject.api.ApiRequestExecutor
import com.example.frontproject.api.ApiService
import com.example.frontproject.data.model.product.AddProductRequest
import com.example.frontproject.data.model.meal.ConsumeProductRequest
import com.example.frontproject.data.model.product.Product
import com.example.frontproject.data.model.product.ProductsResponse
import com.example.frontproject.domain.util.ResourceState


class MealsRepository(
    private val apiRequestExecutor: ApiRequestExecutor,
    private val apiService: ApiService
) {

    suspend fun getProductsByDateRange(from: String, to: String): ResourceState<ProductsResponse> {
        Log.e(
            "MealsRepository",
            "getProductsByDateRange: from = $from, to = $to"
        )
        return apiRequestExecutor.executeHeavyRequest(
            initialCall = {
                apiService.getProductsByDateRange(
                    fromDate = from,
                    toDate = to
                )
            },
            pollingCall = { requestId -> apiService.getProductsByDateRangeResponse(requestId) }
        )
    }

    suspend fun addProduct(
        product: Product
    ): ResourceState<Unit> {
        Log.e(
            "MealsRepository",
            "addProduct: product = $product"
        )
        val addProductRequest = AddProductRequest(
            bcode = product.barcode,
            name = product.name,
            calories = product.calories,
            B = product.proteins,
            Z = product.fats,
            U = product.carbs,
            mass = product.mass,
        )
        val response = apiRequestExecutor.executeRequest {
            apiService.addProduct(
                productRequest = addProductRequest
            )
        }
        return when (response) {
            is ResourceState.Success -> {
                Log.e("MealsRepository", "addProduct: success")
                ResourceState.Success(Unit)
            }

            is ResourceState.Error -> {
                Log.e("MealsRepository", "addProduct: error = ${response.message}")
                ResourceState.Error(response.message)
            }

            ResourceState.Loading -> {
                Log.e("MealsRepository", "addProduct: loading")
                ResourceState.Loading
            }
        }
    }

    suspend fun getProductByBarcode(barcode: String): ResourceState<Product> {
        return apiRequestExecutor.executeHeavyRequest(
            initialCall = { apiService.getProductByBarcode(barcode) },
            pollingCall = { apiService.getProductByBarcodeResponse(it) }
        ).let { result ->
            when (result) {
                is ResourceState.Success -> {
                    when (result.data.status) {
                        "success" -> {
                            val pd = result.data.data!!
                            val product = Product(
                                id = pd.productId,
                                name = pd.name,
                                barcode = barcode.toLongOrNull() ?: 0L,
                                calories = pd.calories.toInt(),
                                proteins = pd.proteins,
                                fats = pd.fats,
                                carbs = pd.carbs,
                                mass = pd.mass.toInt()
                            )
                            ResourceState.Success(product)
                        }

                        "not_found" -> {
                            ResourceState.Error(result.data.errorMessage ?: "Продукт не найден")
                        }

                        else -> ResourceState.Error("Неизвестный статус: ${result.data.status}")
                    }
                }

                is ResourceState.Error -> ResourceState.Error(result.message)
                ResourceState.Loading -> ResourceState.Loading
            }
        }
    }

    suspend fun consumeProduct(request: ConsumeProductRequest): ResourceState<Unit> {
        return apiRequestExecutor.executeRequest {
            apiService.consumeProduct(request)
        }.let {
            when (it) {
                is ResourceState.Success -> ResourceState.Success(Unit)
                is ResourceState.Error -> ResourceState.Error(it.message)
                ResourceState.Loading -> ResourceState.Loading
            }
        }
    }

    suspend fun getProductsByName(name: String): ResourceState<List<Product>> {
        return apiRequestExecutor.executeHeavyRequest(
            initialCall = { apiService.getProductsByName(name) },
            pollingCall = { requestId -> apiService.getProductsByNameResponse(requestId) }
        ).let { responseResult ->
            when (responseResult) {
                is ResourceState.Success -> {
                    val responseDto = responseResult.data

                    // Проверяем статус ответа
                    if (responseDto.status == "not_found") {
                        // Если продукты не найдены, возвращаем успешный результат с пустым списком
                        Log.i(
                            "MealsRepository",
                            "getProductsByName: Products not found for name '$name' (status: not_found). Returning empty list. Message from server: ${responseDto.errorMessage}"
                        )
                        return@let ResourceState.Success(emptyList())
                    }

                    val productsDataContainer = responseDto.data
                    val dtoList = productsDataContainer?.products

                    if (dtoList != null) {
                        val productList = dtoList.map { dto ->
                            Product(
                                id = dto.productId,
                                name = dto.name,
                                barcode = 0L,
                                calories = dto.calories.toInt(),
                                proteins = dto.protein.toFloat(),
                                fats = dto.fat.toFloat(),
                                carbs = dto.carbs.toFloat(),
                                mass = dto.mass.toInt()
                            )
                        }
                        ResourceState.Success(productList)
                    } else {
                        Log.w(
                            "MealsRepository",
                            "getProductsByName: Список продуктов null или контейнер данных null, и статус не был 'not_found'. Возвращаем пустой список."
                        )
                        ResourceState.Success(emptyList())
                    }
                }

                is ResourceState.Error -> ResourceState.Error(responseResult.message)
                is ResourceState.Loading -> ResourceState.Loading
            }
        }
    }
}