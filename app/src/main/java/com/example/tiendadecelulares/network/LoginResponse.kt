package com.example.tiendadecelulares.network

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: User
)

data class User(
    val ID: Int,
    val EMAIL: String,
    val IS_ADMIN: Int,
    val IS_BLOCKED: Int,
    val CREATED_AT: String
)
