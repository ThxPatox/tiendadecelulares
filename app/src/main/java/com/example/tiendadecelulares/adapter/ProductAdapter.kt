package com.example.tiendadecelulares.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiendadecelulares.R
import com.example.tiendadecelulares.model.Product
import java.util.*

class ProductAdapter(
    private var productList: List<Product>,
    private val onItemClicked: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var productListFull: List<Product> = productList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_list_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener { onItemClicked(product) }
    }

    override fun getItemCount(): Int = productList.size

    fun setData(newProductList: List<Product>) {
        productListFull = newProductList
        productList = newProductList
        notifyDataSetChanged()
    }

    // Corregido: El filtro ya no depende de la categoría
    fun filter(query: String?) {
        var filteredList = productListFull

        if (!query.isNullOrEmpty()) {
            val filterPattern = query.lowercase(Locale.getDefault()).trim()
            filteredList = filteredList.filter {
                it.name?.lowercase(Locale.getDefault())?.contains(filterPattern) == true ||
                it.brand?.lowercase(Locale.getDefault())?.contains(filterPattern) == true
            }
        }

        productList = filteredList
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.product_name)
        private val brandTextView: TextView = itemView.findViewById(R.id.product_brand)
        private val priceTextView: TextView = itemView.findViewById(R.id.product_price)
        private val imageView: ImageView = itemView.findViewById(R.id.product_image)

        fun bind(product: Product) {
            nameTextView.text = product.name
            brandTextView.text = product.brand
            priceTextView.text = "$${product.price?.div(100.0)}"
        
            // Cargar la primera imagen con Glide
            val imageUrl = product.imageUrls?.firstOrNull() ?: "" // toma la primera imagen
            Glide.with(itemView.context)
                .load(imageUrl)
                .into(imageView)

        }
    }
}