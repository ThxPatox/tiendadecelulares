package com.example.tiendadecelulares.repository

import com.example.tiendadecelulares.db.CartDao
import com.example.tiendadecelulares.db.CartItemEntity
import com.example.tiendadecelulares.model.Product
import com.example.tiendadecelulares.network.XanoApiService
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao, private val xanoApiService: XanoApiService) {

    fun getCartItems(): Flow<List<CartItemEntity>> = cartDao.getAllCartItems()

    suspend fun addToCart(product: Product, quantity: Int) {
        val existingItem = cartDao.getItemById(product.id)
        if (existingItem != null) {
            cartDao.updateQuantity(product.id, existingItem.quantity + quantity)
        } else {
            val cartItem = CartItemEntity(
                productId = product.id,
                name = product.name ?: "",
                brand = product.brand ?: "",
                price = product.price ?: 0,
                imageUrl = product.imageUrls?.firstOrNull(),
                quantity = quantity
            )
            cartDao.insertOrUpdateItem(cartItem)
        }
    }

    suspend fun removeFromCart(productId: Int) {
        cartDao.deleteItem(productId)
    }

    suspend fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity > 0) {
            cartDao.updateQuantity(productId, quantity)
        } else {
            cartDao.deleteItem(productId)
        }
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // TODO: Add synchronization logic with the remote server
}