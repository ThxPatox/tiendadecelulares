package com.example.tiendadecelulares.network

import com.example.tiendadecelulares.model.*
import retrofit2.http.*

interface XanoAuthApiService {
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}
