package com.example.tiendadecelulares

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendadecelulares.adapter.ImagePreviewAdapter
import com.example.tiendadecelulares.model.Product
import com.example.tiendadecelulares.network.RetrofitInstance
import kotlinx.coroutines.launch

class AddEditProductActivity : AppCompatActivity() {

    private var productId: Int? = null
    private val selectedImageUris = mutableListOf<Uri>()

    private lateinit var nameEditText: EditText
    private lateinit var brandEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var stockEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var imagePreviewAdapter: ImagePreviewAdapter

    private val selectImages = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        uris: List<Uri> ->
        selectedImageUris.addAll(uris)
        imagePreviewAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_product)

        productId = intent.extras?.getInt("PRODUCT_ID")

        nameEditText = findViewById(R.id.product_name_edit_text)
        brandEditText = findViewById(R.id.product_brand_edit_text)
        descriptionEditText = findViewById(R.id.product_description_edit_text)
        priceEditText = findViewById(R.id.product_price_edit_text)
        stockEditText = findViewById(R.id.product_stock_edit_text)
        categoryEditText = findViewById(R.id.product_category_edit_text)

        setupImagePreview()

        if (productId != null) {
            fetchProductDetails(productId!!)
        }

        findViewById<Button>(R.id.select_images_button).setOnClickListener {
            selectImages.launch("image/*")
        }

        findViewById<Button>(R.id.save_product_button).setOnClickListener {
            saveProduct()
        }
    }

    private fun setupImagePreview() {
        val recyclerView: RecyclerView = findViewById(R.id.image_preview_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imagePreviewAdapter = ImagePreviewAdapter(selectedImageUris)
        recyclerView.adapter = imagePreviewAdapter
    }

    private fun fetchProductDetails(id: Int) {
        lifecycleScope.launch {
            try {
                val product = RetrofitInstance.api.getProductById(id)
                nameEditText.setText(product.name)
                brandEditText.setText(product.brand)
                descriptionEditText.setText(product.description)
                priceEditText.setText(product.price.toString())
                stockEditText.setText(product.stock.toString())
                categoryEditText.setText(product.category)
                product.imageUrls?.let { urls ->
                    selectedImageUris.addAll(urls.map { Uri.parse(it) })
                    imagePreviewAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("AddEditProductActivity", "Error fetching product", e)
            }
        }
    }

    private fun saveProduct() {
        val name = nameEditText.text.toString()
        val brand = brandEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val price = priceEditText.text.toString().toIntOrNull()
        val stock = stockEditText.text.toString().toIntOrNull()
        val category = categoryEditText.text.toString()

        var imageUrls: List<String> = selectedImageUris.map { it.toString() }

        if (name.contains("xiaomi 17 pm", ignoreCase = true)) {
            imageUrls = listOf(
                "https://images.indianexpress.com/2025/09/Xiaomi-17.png",
                "https://www.mobiledokan.com/media/xiaomi-17-pro-max-purple-official-image.webp"
            )
        } else if (name.contains("samsung s25", ignoreCase = true)) {
            imageUrls = listOf(
                "https://files.tecnoblog.net/wp-content/uploads/2025/01/galaxy-s25-plus-azul-gelo.png",
                "https://media.power-cdn.net/images/h-40eabea1dfe14b75b9405b4bd3543315/feelingimages/3832139/3832139_1_1200px_w_g.jpg"
            )
        }

        val product = Product(
            id = productId ?: 0,
            name = name,
            brand = brand,
            description = description,
            price = price,
            stock = stock,
            category = category,
            imageUrls = imageUrls,    // tu lista de URLs
            createdAt = null
        )


        lifecycleScope.launch {
            try {
                if (productId == null) {
                    RetrofitInstance.api.createProduct(product)
                    Toast.makeText(this@AddEditProductActivity, "Producto creado", Toast.LENGTH_SHORT).show()
                } else {
                    RetrofitInstance.api.updateProduct(productId!!, product)
                    Toast.makeText(this@AddEditProductActivity, "Producto actualizado", Toast.LENGTH_SHORT).show()
                }
                finish()
            } catch (e: Exception) {
                Log.e("AddEditProductActivity", "Error saving product", e)
                Toast.makeText(this@AddEditProductActivity, "Error al guardar el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
