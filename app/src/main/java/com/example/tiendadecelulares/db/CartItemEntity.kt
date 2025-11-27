package com.example.tiendadecelulares.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    val productId: Int,
    val name: String,
    val brand: String,
    val price: Int,
    val imageUrl: String?,
    var quantity: Int
)
