package com.example.tiendadecelulares.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiendadecelulares.R
import com.example.tiendadecelulares.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderHistoryAdapter(private var orders: List<Order>) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount() = orders.size

    fun updateData(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.order_product_name)
        private val orderQuantity: TextView = itemView.findViewById(R.id.order_quantity)
        private val orderDate: TextView = itemView.findViewById(R.id.order_date)
        private val productImage: ImageView = itemView.findViewById(R.id.order_product_image)

        fun bind(order: Order) {
            order.productDetails?.let { product ->
                productName.text = product.name ?: "Nombre no disponible"
                orderQuantity.text = "Cantidad: ${order.quantity}"
                orderDate.text = formatDate(order.createdAt)

                // Corregido: Usar imageUrl (String) en lugar de imageUrls (List)
                //product.imageUrl?.let { imageUrl ->
                //    Glide.with(itemView.context)
               //         .load(imageUrl)
                //        .placeholder(R.drawable.ic_launcher_background) // Opcional: un placeholder
               //         .into(productImage)
                //}
            } ?: run {
                // Maneja el caso en que productDetails es nulo
                productName.text = "Producto no disponible"
                orderQuantity.text = ""
                orderDate.text = ""
                productImage.setImageDrawable(null)
            }
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(timestamp)
            return sdf.format(date)
        }
    }
}