package com.example.tiendadecelulares.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiendadecelulares.R
import com.example.tiendadecelulares.db.CartItemEntity

class CartAdapter(
    private var cartItems: List<CartItemEntity>,
    private val onIncrease: (CartItemEntity) -> Unit,
    private val onDecrease: (CartItemEntity) -> Unit,
    private val onDelete: (CartItemEntity) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.bind(item)
        holder.itemView.findViewById<Button>(R.id.increase_quantity_button).setOnClickListener { onIncrease(item) }
        holder.itemView.findViewById<Button>(R.id.decrease_quantity_button).setOnClickListener { onDecrease(item) }
        holder.itemView.findViewById<Button>(R.id.delete_item_button).setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateData(newItems: List<CartItemEntity>) {
        cartItems = newItems
        notifyDataSetChanged()
    }

    fun getItems(): List<CartItemEntity> = cartItems

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.cart_product_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.cart_product_price)
        private val quantityTextView: TextView = itemView.findViewById(R.id.cart_product_quantity)
        private val imageView: ImageView = itemView.findViewById(R.id.cart_product_image)

        fun bind(item: CartItemEntity) {
            nameTextView.text = item.name
            priceTextView.text = "$${item.price / 100.0}"
            quantityTextView.text = "Cantidad: ${item.quantity}"
            Glide.with(itemView.context).load(item.imageUrl).into(imageView)
        }
    }
}