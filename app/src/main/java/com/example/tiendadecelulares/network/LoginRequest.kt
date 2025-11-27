package com.example.tiendadecelulares.network

// Define la estructura de datos que enviaremos a Xano al hacer login.
// Debe coincidir exactamente con los campos que espera la API.
data class LoginRequest(
    val EMAIL: String,
    val PASSWORD: String
)
