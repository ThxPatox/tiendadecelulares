package com.example.tiendadecelulares.model

// Este modelo representa un producto dentro del carrito de compras.
// Incluye todos los detalles del producto y la cantidad añadida.
data class CartProduct(
    val id: Int, // ID del registro en la tabla 'cart'
    val product_id: Int,
    val quantity: Int,
    val product: Product // Objeto anidado con los detalles del producto
)
