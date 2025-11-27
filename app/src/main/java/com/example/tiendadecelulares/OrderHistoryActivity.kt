package com.example.tiendadecelulares

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendadecelulares.adapter.OrderHistoryAdapter
import com.example.tiendadecelulares.network.RetrofitInstance
import kotlinx.coroutines.launch

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var orderHistoryRecyclerView: RecyclerView
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyHistoryTextView: TextView
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        val toolbar: Toolbar = findViewById(R.id.toolbar_order_history)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        userId = intent.getIntExtra("USER_ID", -1)

        progressBar = findViewById(R.id.order_history_progress_bar)
        emptyHistoryTextView = findViewById(R.id.empty_order_history_text)

        setupRecyclerView()

        if (userId != -1) {
            fetchOrderHistory(userId)
        }
    }

    private fun setupRecyclerView() {
        orderHistoryRecyclerView = findViewById(R.id.order_history_recycler_view)
        orderHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        orderHistoryAdapter = OrderHistoryAdapter(emptyList())
        orderHistoryRecyclerView.adapter = orderHistoryAdapter
    }

    private fun fetchOrderHistory(userId: Int) {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val orders = RetrofitInstance.api.getOrdersByUserId(userId)
                showLoading(false)
                if (orders.isEmpty()) {
                    showEmptyMessage(true)
                } else {
                    showEmptyMessage(false)
                    orderHistoryAdapter.updateData(orders)
                }
            } catch (e: Exception) {
                showLoading(false)
                showEmptyMessage(true, "Error al cargar el historial")
                Log.e("OrderHistoryActivity", "Error al obtener el historial de pedidos", e)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        orderHistoryRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showEmptyMessage(show: Boolean, message: String = "No tienes pedidos anteriores") {
        emptyHistoryTextView.text = message
        emptyHistoryTextView.visibility = if (show) View.VISIBLE else View.GONE
        orderHistoryRecyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}