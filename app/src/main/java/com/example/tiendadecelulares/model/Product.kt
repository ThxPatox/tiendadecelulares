package com.example.tiendadecelulares.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("ID") val id: Int,
    @SerializedName("NAME") val name: String?,
    @SerializedName("BRAND") val brand: String?,
    @SerializedName("DESCRIPTION") val description: String?,
    @SerializedName("PRICE") val price: Int?,
    @SerializedName("STOCK") val stock: Int?,
    @SerializedName("CATEGORY") val category: String?,
    @SerializedName("CREATED_AT") val createdAt: Long?,
    @SerializedName("imageUrls") val imageUrls: List<String>?
)


