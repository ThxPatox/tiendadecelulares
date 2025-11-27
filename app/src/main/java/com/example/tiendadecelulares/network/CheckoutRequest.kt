package com.example.tiendadecelulares.network

// Item individual en el request de checkout
data class CheckoutItem(
    val product_id: Int,
    val quantity: Int
)

// Define la estructura de datos que se enviará a Xano para procesar un checkout.
data class CheckoutRequest(
    val user_id: Int,
    val items: List<CheckoutItem>
)
