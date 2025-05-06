package com.example.frontproject.data.model

data class ConsumeProductRequest(
    val productId: Int,
    val time: String,
    val massConsumed: Int
)

data class ConsumeProductResponse(
    val message: String
)