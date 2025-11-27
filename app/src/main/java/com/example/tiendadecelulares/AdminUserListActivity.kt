package com.example.tiendadecelulares

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendadecelulares.adapter.AdminUserAdapter
import com.example.tiendadecelulares.network.RetrofitInstance
import com.example.tiendadecelulares.network.User
import kotlinx.coroutines.launch

class AdminUserListActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: AdminUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_user_list)

        setupRecyclerView()
        fetchUsers()
    }

    private fun setupRecyclerView() {
        userRecyclerView = findViewById(R.id.admin_user_recycler_view)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = AdminUserAdapter(emptyList()) { user ->
            toggleUserBlock(user)
        }
        userRecyclerView.adapter = userAdapter
    }

    private fun fetchUsers() {
        lifecycleScope.launch {
            try {
                val userList = RetrofitInstance.api.getUsers()
                userAdapter.setData(userList)
            } catch (e: Exception) {
                Log.e("AdminUserListActivity", "Error al obtener los usuarios", e)
            }
        }
    }

    private fun toggleUserBlock(user: User) {
        lifecycleScope.launch {
            try {
                //val updatedUser = user.copy(isBlocked = !(user.isBlocked ?: false))
                //RetrofitInstance.api.updateUser(user.id, updatedUser)
                fetchUsers()
            } catch (e: Exception) {
                Log.e("AdminUserListActivity", "Error al actualizar el usuario", e)
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}