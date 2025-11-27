package com.example.tiendadecelulares.model

import com.google.gson.annotations.SerializedName

// Representa un único item en el historial de pedidos de un usuario.
data class Order(
    val id: Int, // El ID del pedido
    val quantity: Int,
    @SerializedName("created_at")
    val createdAt: Long, // Usamos Long para el timestamp de la compra
    @SerializedName("product_details")
    val productDetails: Product // Objeto anidado con los detalles del producto comprado
)
