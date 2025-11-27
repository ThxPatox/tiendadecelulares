package com.example.tiendadecelulares.model

data class CartItem(
    val id: Int,
    val user_id: Int,
    val product_id: Int,
    val quantity: Int
)