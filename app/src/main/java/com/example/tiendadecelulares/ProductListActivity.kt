package com.example.tiendadecelulares

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendadecelulares.adapter.ProductAdapter
import com.example.tiendadecelulares.network.RetrofitInstance
import com.example.tiendadecelulares.util.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ProductListActivity : AppCompatActivity() {

    private lateinit var productRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyListTextView: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var fabCart: FloatingActionButton
    private lateinit var fabAddProduct: FloatingActionButton

    private var searchQuery: String? = null
    private var userId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        sessionManager = SessionManager(this)
        userId = sessionManager.fetchUserId().toLong() // asegúrate que el sessionManager devuelva Long o conviértelo aquí

        progressBar = findViewById(R.id.progress_bar)
        emptyListTextView = findViewById(R.id.empty_list_text)
        fabCart = findViewById(R.id.fab_cart)
        fabAddProduct = findViewById(R.id.fab_add_product)

        setupRecyclerView()
        setupFabs()
        fetchProducts()
    }

    private fun setupRecyclerView() {
        productRecyclerView = findViewById(R.id.product_recycler_view)
        productRecyclerView.layoutManager = GridLayoutManager(this, 1)
        productAdapter = ProductAdapter(emptyList()) { product ->
            val intent = Intent(this@ProductListActivity, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id) // ahora product.id es Long
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        productRecyclerView.adapter = productAdapter
    }

    private fun setupFabs() {
        fabCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        if (sessionManager.fetchUserRole() == "admin") {
            fabAddProduct.visibility = View.VISIBLE
            fabAddProduct.setOnClickListener {
                val intent = Intent(this, AddEditProductActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        } else {
            fabAddProduct.visibility = View.GONE
        }
    }

    private fun fetchProducts() {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val productListResponse = RetrofitInstance.api.getProducts()

                Log.d("AdminProductListActivity", "pato productList: $productListResponse")
                val productList = productListResponse.items // Esta sí es List<Product>

                showLoading(false)
                if (productList.isEmpty()) {
                    showEmptyMessage(true)
                } else {
                    showEmptyMessage(false)
                    productAdapter.setData(productList)
                }

            } catch (e: Exception) {
                showLoading(false)
                showEmptyMessage(true, "Error al cargar los productos")
                Log.e("ProductListActivity", "Error al obtener los productos", e)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        if(isLoading) {
            productRecyclerView.visibility = View.GONE
            emptyListTextView.visibility = View.GONE
        }
    }

    private fun showEmptyMessage(show: Boolean, message: String = "No hay productos disponibles") {
        emptyListTextView.text = message
        emptyListTextView.visibility = if (show) View.VISIBLE else View.GONE
        productRecyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText
                productAdapter.filter(searchQuery)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                performLogout()
                true
            }
            R.id.action_order_history -> {
                val intent = Intent(this, OrderHistoryActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performLogout() {
        sessionManager.clearSession()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}
