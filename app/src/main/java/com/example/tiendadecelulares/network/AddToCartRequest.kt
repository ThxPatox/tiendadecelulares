package com.example.tiendadecelulares.network

data class AddToCartRequest(
    val user_id: Int,
    val product_id: Int,
    val quantity: Int
)