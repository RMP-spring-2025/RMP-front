package com.example.frontproject.data.model.meal

data class ConsumeProductRequest(
    val productId: Int,
    val time: String,
    val massConsumed: Int
)

data class ConsumeProductResponse(
    val message: String
)