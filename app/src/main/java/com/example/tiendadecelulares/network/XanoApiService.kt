package com.example.tiendadecelulares.network

import com.example.tiendadecelulares.model.*
import retrofit2.http.*

interface XanoApiService {

    @POST("auth/signup")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("user")
    suspend fun getUsers(): List<User>

    @PUT("user/{user_id}")
    suspend fun updateUser(@Path("user_id") userId: Int, @Body user: User): User

    @GET("orders/{user_id}")
    suspend fun getOrdersByUserId(@Path("user_id") userId: Int): List<Order>

    @PUT("cart/{cart_id}")
    suspend fun updateCartItem(@Path("cart_id") cartId: Int, @Body updateRequest: UpdateCartRequest)

    @DELETE("cart/{cart_id}")
    suspend fun deleteCartItem(@Path("cart_id") cartId: Int)

    @POST("checkout")
    suspend fun checkout(@Body checkoutRequest: CheckoutRequest): CheckoutResponse

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): ProductsResponse

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product

    @POST("products")
    suspend fun createProduct(@Body product: Product): Product

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: Product): Product

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int)

    @GET("categories")
    suspend fun getCategories(): List<String>

    @POST("cart")
    suspend fun addToCart(@Body addToCartRequest: AddToCartRequest): CartItem

    @GET("cart/{user_id}")
    suspend fun getCartByUserId(@Path("user_id") userId: Int): List<CartProduct>
}
