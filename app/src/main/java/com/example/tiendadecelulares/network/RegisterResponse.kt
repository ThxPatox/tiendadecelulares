package com.example.tiendadecelulares.network

// Define la estructura de la respuesta que se recibe de Xano al registrar un usuario.
data class RegisterResponse(
    val authToken: String,
    val user: User
)
