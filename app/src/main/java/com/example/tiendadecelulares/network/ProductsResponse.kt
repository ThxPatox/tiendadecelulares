package com.example.tiendadecelulares.network

import com.example.tiendadecelulares.model.Product

// Define la estructura de la respuesta que se recibe de Xano al registrar un usuario.
data class ProductsResponse(
    val items: List<Product>,
    val total: Int,
    val page: Int,
    val limit: Int
)
