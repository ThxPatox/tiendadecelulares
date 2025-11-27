package com.example.tiendadecelulares

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendadecelulares.adapter.CartAdapter
import com.example.tiendadecelulares.db.AppDatabase
import com.example.tiendadecelulares.network.CheckoutItem
import com.example.tiendadecelulares.network.CheckoutRequest
import com.example.tiendadecelulares.network.RetrofitInstance
import com.example.tiendadecelulares.repository.CartRepository
import com.example.tiendadecelulares.util.SessionManager
import com.example.tiendadecelulares.viewmodel.CartViewModel
import com.example.tiendadecelulares.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyCartTextView: TextView
    private lateinit var sessionManager: SessionManager

    private val cartViewModel: CartViewModel by viewModels {
        ViewModelFactory(CartRepository(AppDatabase.getDatabase(applicationContext).cartDao(), RetrofitInstance.api))
    }

    companion object {
        private const val LOGIN_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        sessionManager = SessionManager(this)

        progressBar = findViewById(R.id.cart_progress_bar)
        emptyCartTextView = findViewById(R.id.empty_cart_text)

        setupRecyclerView()
        observeCartItems()

        val checkoutButton: Button = findViewById(R.id.checkout_button)
        checkoutButton.setOnClickListener {
            val userId = sessionManager.fetchUserId()
            if (userId != -1L) {
                performCheckout(userId.toInt())
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivityForResult(intent, LOGIN_REQUEST_CODE)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val userId = sessionManager.fetchUserId()
            if (userId != -1L) {
                performCheckout(userId.toInt())
            }
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(emptyList(),
            onIncrease = { item -> cartViewModel.updateQuantity(item.productId, item.quantity + 1) },
            onDecrease = { item -> cartViewModel.updateQuantity(item.productId, item.quantity - 1) },
            onDelete = { item -> cartViewModel.removeFromCart(item.productId) }
        )
        cartRecyclerView = findViewById(R.id.cart_recycler_view)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter
    }

    private fun observeCartItems() {
        lifecycleScope.launch {
            cartViewModel.cartItems.collectLatest { items ->
                cartAdapter.updateData(items)
                showEmptyMessage(items.isEmpty())
            }
        }
    }

    private fun showEmptyMessage(show: Boolean, message: String = "Tu carrito está vacío") {
        emptyCartTextView.text = message
        emptyCartTextView.visibility = if (show) View.VISIBLE else View.GONE
        cartRecyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun performCheckout(userId: Int) {
        lifecycleScope.launch {
            val cartItems = cartViewModel.cartItems.value
            if (cartItems.isNotEmpty()) {
                val checkoutItems = cartItems.map { CheckoutItem(it.productId, it.quantity) }
                val request = CheckoutRequest(user_id = userId, items = checkoutItems)

                try {
                    val response = RetrofitInstance.api.checkout(request)
                    Toast.makeText(this@CartActivity, response.message, Toast.LENGTH_LONG).show()
                    cartViewModel.clearCart()
                    val intent = Intent(this@CartActivity, ProductListActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                } catch (e: Exception) {
                    Toast.makeText(this@CartActivity, "Error en el checkout: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}