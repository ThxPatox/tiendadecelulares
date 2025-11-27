package com.example.tiendadecelulares

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.tiendadecelulares.adapter.ProductImageAdapter
import com.example.tiendadecelulares.db.AppDatabase
import com.example.tiendadecelulares.model.Product
import com.example.tiendadecelulares.network.RetrofitInstance
import com.example.tiendadecelulares.repository.CartRepository
import com.example.tiendadecelulares.viewmodel.CartViewModel
import com.example.tiendadecelulares.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private var product: Product? = null

    private val cartViewModel: CartViewModel by viewModels {
        ViewModelFactory(CartRepository(AppDatabase.getDatabase(applicationContext).cartDao(), RetrofitInstance.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val productId = intent.getIntExtra("PRODUCT_ID", 0)

        fetchProductDetails(productId)

        findViewById<Button>(R.id.add_to_cart_button).setOnClickListener {
            product?.let { 
                addToCart(it)
            }
        }

        findViewById<Button>(R.id.buy_now_button).setOnClickListener {
            // TODO: Implement buy now functionality
            Toast.makeText(this, "Funcionalidad de comprar ahora no implementada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchProductDetails(productId: Int) {
        lifecycleScope.launch {
            try {
                val fetchedProduct = RetrofitInstance.api.getProductById(productId)
                product = fetchedProduct
                updateUI(fetchedProduct)
            } catch (e: Exception) {
                Log.e("ProductDetailActivity", "Error fetching product details", e)
            }
        }
    }

    private fun updateUI(product: Product) {
        findViewById<TextView>(R.id.product_name_text_view).text = product.name
        findViewById<TextView>(R.id.product_price_text_view).text = "$${product.price}"
        findViewById<TextView>(R.id.product_description_text_view).text = product.description

        val imageViewPager = findViewById<ViewPager2>(R.id.product_image_view_pager)
        val imageAdapter = ProductImageAdapter(product.imageUrls ?: emptyList())
        imageViewPager.adapter = imageAdapter
    }

    private fun addToCart(product: Product) {
        lifecycleScope.launch {
            try {
                cartViewModel.addToCart(product, 1)
                Toast.makeText(this@ProductDetailActivity, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("ProductDetailActivity", "Error adding product to cart", e)
                Toast.makeText(this@ProductDetailActivity, "Error al agregar el producto al carrito", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
