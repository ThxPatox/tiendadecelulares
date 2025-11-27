package com.example.tiendadecelulares

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val manageUsersButton: Button = findViewById(R.id.manage_users_button)
        val manageProductsButton: Button = findViewById(R.id.manage_products_button)
        val manageOrdersButton: Button = findViewById(R.id.manage_orders_button)

        manageUsersButton.setOnClickListener {
            val intent = Intent(this, AdminUserListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        manageProductsButton.setOnClickListener {
            val intent = Intent(this, AdminProductListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        manageOrdersButton.setOnClickListener {
            // TODO: Navigate to an activity to manage orders
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}