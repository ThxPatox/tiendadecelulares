package com.example.tiendadecelulares

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.tiendadecelulares.network.RegisterRequest
import com.example.tiendadecelulares.network.RegisterResponse
import com.example.tiendadecelulares.network.RetrofitInstance
import com.example.tiendadecelulares.util.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sessionManager = SessionManager(this)

        val nameEditText = findViewById<EditText>(R.id.name_edit_text)
        val emailEditText = findViewById<EditText>(R.id.email_edit_text)
        val passwordEditText = findViewById<EditText>(R.id.password_edit_text)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirm_password_edit_text)
        val registerButton = findViewById<Button>(R.id.register_button)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Validación
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Deshabilitar botón para evitar doble envío
            registerButton.isEnabled = false

            // Registro
            lifecycleScope.launch {
                performRegistration(email, password, false) // si quieres usar name, adapta request/endpoint
                registerButton.isEnabled = true
            }
        }
    }

    private suspend fun performRegistration(email: String, password: String, isAdmin: Boolean) {
        try {
            // Construir request con los nombres que el backend espera (usa @SerializedName en la data class)
            val request = RegisterRequest(
                EMAIL = email.trim(),
                PASSWORD = password,
                IS_ADMIN = if (isAdmin) 1 else 0
            )

            // Llamada al API (se asume que devuelve RegisterResponse)
            val response: RegisterResponse = RetrofitInstance.api.register(request)
            Log.d("RegisterActivity", "Register response body: $response")

            // Si el backend incluye un campo 'success' y lo usa, validarlo
            // (si no existe, omite esta comprobación)
            try {
                val successField = response::class.members.firstOrNull { it.name == "success" }
                if (successField != null) {
                    val success = response::class.java.getMethod("getSuccess").invoke(response) as? Boolean
                    if (success == false) {
                        val msg = response::class.java.getMethod("getMessage").invoke(response) as? String
                        Toast.makeText(this, msg ?: "Error en registro", Toast.LENGTH_LONG).show()
                        return
                    }
                }
            } catch (ignore: Exception) {
                // Si la reflexión falla, no problem — seguimos
            }

            // Guardar sesión con la nueva firma: (userId: Long, email: String, isAdmin: Int)
            val user = response.user
            sessionManager.saveSession(
                user.ID.toLong(),
                user.EMAIL,
                user.IS_ADMIN
            )

            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()

            if (callingActivity != null) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                val intent = Intent(this, ProductListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

        } catch (e: HttpException) {
            Log.e("RegisterActivity", "HTTP error in registration", e)
            val msg = e.response()?.errorBody()?.string()
            Toast.makeText(this, "Error en el registro: ${msg ?: e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("RegisterActivity", "Error en el registro", e)
            Toast.makeText(this, "Error en el registro: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
