package com.example.tiendadecelulares

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendadecelulares.adapter.AdminProductAdapter
import com.example.tiendadecelulares.network.RetrofitInstance
import kotlinx.coroutines.launch

class AdminProductListActivity : AppCompatActivity() {

    private lateinit var productRecyclerView: RecyclerView
    private lateinit var productAdapter: AdminProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_product_list)

        setupRecyclerView()
        fetchProducts()

        findViewById<android.widget.Button>(R.id.add_product_button).setOnClickListener {
            val intent = Intent(this, AddEditProductActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun setupRecyclerView() {
        productRecyclerView = findViewById(R.id.admin_product_recycler_view)
        productRecyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = AdminProductAdapter(emptyList(),
            onEditClicked = { product ->
                val intent = Intent(this, AddEditProductActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            },
            onDeleteClicked = { product ->
                lifecycleScope.launch {
                    try {
                        RetrofitInstance.api.deleteProduct(product.id)
                        fetchProducts()
                    } catch (e: Exception) {
                        Log.e("AdminProductListActivity", "Error al eliminar el producto", e)
                    }
                }
            }
        )
        productRecyclerView.adapter = productAdapter
    }

    private fun fetchProducts() {
        lifecycleScope.launch {
            try {
                val productList = RetrofitInstance.api.getProducts() // esto ya es List<Product>
                Log.d("AdminProductListActivity", "pato productList: $productList")
                productAdapter.setData(productList.items) // ✅ usar directamente
            } catch (e: Exception) {
                Log.e("AdminProductListActivity", "Error al obtener los productos", e)
            }
        }
    }



    override fun onResume() {
        super.onResume()
        fetchProducts()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}