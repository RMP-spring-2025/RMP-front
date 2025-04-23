package com.example.frontproject.data.remote
// пример API с использованием Retrofit
import com.example.frontproject.data.model.DayMeals
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MealsApi {
    @GET("user/products")
    suspend fun initProductsRequest(
        @Query("from") from: String,
        @Query("to") to: String
    ): RequestIdResponse

    @POST("heavy_response/{id}")
    suspend fun getHeavyResponse(@Path("id") requestId: String): Response<DayMeals>
}

data class RequestIdResponse(val id: String)