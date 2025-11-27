package com.example.tiendadecelulares.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiendadecelulares.databinding.ItemAdminProductBinding
import com.example.tiendadecelulares.model.Product
import com.example.tiendadecelulares.network.ProductsResponse

class AdminProductAdapter(
    private var productList: List<Product>,
    private val onEditClicked: (Product) -> Unit,
    private val onDeleteClicked: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemAdminProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
        holder.binding.editProductButton.setOnClickListener { onEditClicked(product) }
        holder.binding.deleteProductButton.setOnClickListener { onDeleteClicked(product) }
    }

    override fun getItemCount(): Int = productList.size

    // 🔹 Cambiado para recibir List<Product> directamente
    fun setData(newProductList: List<Product>) {
        productList = newProductList
        notifyDataSetChanged()
    }

    class ProductViewHolder(val binding: ItemAdminProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.productName.text = product.name
            binding.productBrand.text = product.brand
            // 🔹 Si tu Product tiene imageUrls como List<String>
            product.imageUrls?.firstOrNull()?.let {
                Glide.with(itemView.context).load(it).into(binding.productImage)
            }
        }
    }
}
