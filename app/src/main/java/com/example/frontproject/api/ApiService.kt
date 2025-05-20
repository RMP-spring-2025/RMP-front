package com.example.frontproject.api

import com.example.frontproject.data.model.BzuStatsResponse
import com.example.frontproject.data.model.product.AddProductRequest
import com.example.frontproject.data.model.auth.AuthRequest
import com.example.frontproject.data.model.auth.AuthResponse
import com.example.frontproject.data.model.CaloriesStatsResponse
import com.example.frontproject.data.model.meal.ConsumeProductRequest
import com.example.frontproject.data.model.meal.ConsumeProductResponse
import com.example.frontproject.data.model.auth.CreateUserProfileRequest
import com.example.frontproject.data.model.auth.CreateUserProfileResponse
import com.example.frontproject.data.model.product.ProductResponseByBcode
import com.example.frontproject.data.model.ProductsByNameResponseDto // Убедитесь, что импорт правильный
import com.example.frontproject.data.model.RequestIdResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

import com.example.frontproject.data.model.product.ProductsResponse
import com.example.frontproject.data.model.stats.UserProfileStatsResponse


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

    @GET("products/name/{productName}")
    suspend fun getProductsByName(@Path("productName") productName: String): Response<RequestIdResponse>

    @GET("heavy_response/{id}")
    suspend fun getProductsByNameResponse(@Path("id") requestId: String): Response<ProductsByNameResponseDto>

    @POST("user/product")
    suspend fun consumeProduct(
        @Body request: ConsumeProductRequest
    ): Response<ConsumeProductResponse>

    @POST("auth")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>

    @POST("user/create")
    suspend fun createUserProfile(@Body request: CreateUserProfileRequest): Response<CreateUserProfileResponse>

    @GET("user/calories")
    suspend fun getCaloriesForRange(
        @Query("from") fromDate: String,
        @Query("to") toDate: String
    ): Response<RequestIdResponse>

    @GET("user/bzu")
    suspend fun getBzuForRange(
        @Query("from") fromDate: String,
        @Query("to") toDate: String
    ): Response<RequestIdResponse>

    @GET("heavy_response/{id}")
    suspend fun getBzuResponse(@Path("id") requestId: String): Response<BzuStatsResponse>

    @GET("heavy_response/{id}")
    suspend fun getCaloriesResponse(@Path("id") requestId: String): Response<CaloriesStatsResponse>

    @GET("user/stat")
    suspend fun getUserStats(): Response<RequestIdResponse>

    @GET("heavy_response/{id}")
    suspend fun getUserStatsResponse(@Path("id") requestId: String): Response<UserProfileStatsResponse>

}