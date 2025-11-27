package com.example.tiendadecelulares.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendadecelulares.db.CartItemEntity
import com.example.tiendadecelulares.model.Product
import com.example.tiendadecelulares.repository.CartRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository) : ViewModel() {

    val cartItems: StateFlow<List<CartItemEntity>> = repository.getCartItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addToCart(product: Product, quantity: Int) = viewModelScope.launch {
        repository.addToCart(product, quantity)
    }

    fun removeFromCart(productId: Int) = viewModelScope.launch {
        repository.removeFromCart(productId)
    }

    fun updateQuantity(productId: Int, quantity: Int) = viewModelScope.launch {
        repository.updateQuantity(productId, quantity)
    }

    fun clearCart() = viewModelScope.launch {
        repository.clearCart()
    }
}
