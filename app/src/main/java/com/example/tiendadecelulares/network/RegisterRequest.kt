package com.example.tiendadecelulares.network

// Define la estructura de datos que se enviará a Xano para registrar un nuevo usuario.
data class RegisterRequest(
    val EMAIL: String,
    val PASSWORD: String,
    val IS_ADMIN: Int
)
