package com.example.frontproject.api

import com.example.frontproject.data.model.AddProductRequest
import com.example.frontproject.data.model.AuthRequest
import com.example.frontproject.data.model.AuthResponse
import com.example.frontproject.data.model.ConsumeProductRequest
import com.example.frontproject.data.model.ConsumeProductResponse
import com.example.frontproject.data.model.ProductResponseByBcode
import com.example.frontproject.data.model.RequestIdResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

import com.example.frontproject.data.model.ProductsResponse


data class RefreshTokenRequest(val token: String)
data class RefreshTokenResponse(val token: String)


interface ApiService {

    @POST("refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    @GET("user/products")
    suspend fun getProductsByDateRange(
        @Query("from") fromDate: String,
        @Query("to") toDate: String
    ): Response<RequestIdResponse>

    @GET("products/bcode/{barcode}")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): Response<RequestIdResponse>

    @GET("heavy_response/{id}")
    suspend fun getProductByBarcodeResponse(@Path("id") requestId: String): Response<ProductResponseByBcode>

    @GET("heavy_response/{id}")
    suspend fun getProductsByDateRangeResponse(@Path("id") requestId: String): Response<ProductsResponse>


    @POST("products")
    suspend fun addProduct(@Body productRequest: AddProductRequest): Response<Unit> // Или Response<Void> если сервер ничего не возвращает в теле

    @POST("user/product")
    suspend fun consumeProduct(
        @Body request: ConsumeProductRequest
    ): Response<ConsumeProductResponse>



    @POST("auth") // Укажите здесь ваш эндпоинт авторизации
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>
}