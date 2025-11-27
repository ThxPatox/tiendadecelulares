package com.example.tiendadecelulares

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.tiendadecelulares.databinding.ActivityMainBinding
import com.example.tiendadecelulares.network.LoginRequest
import com.example.tiendadecelulares.network.LoginResponse
import com.example.tiendadecelulares.network.RetrofitInstance
import com.example.tiendadecelulares.util.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException

// arriba de la clase MainActivity
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    // dentro de la clase MainActivity
    private var isLoggingIn = false

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    companion object {
        private const val REGISTER_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        // --- Cambio: ya no usamos fetchAuthToken() porque el backend no lo devuelve ---
        // Comprobamos si el usuario ya está logueado revisando userId guardado
        val storedUserId = sessionManager.fetchUserId()
        if (storedUserId != -1L && callingActivity == null) {
            navigateToCorrectActivity()
            return // Evita que se ejecute el resto del onCreate
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    performLogin(email, password)
                }
            } else {
                Toast.makeText(this, "Por favor, ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            if (callingActivity != null) {
                startActivityForResult(intent, REGISTER_REQUEST_CODE)
            } else {
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }
    }

    private suspend fun performLogin(email: String, password: String) {
        // prevención doble envío
        if (isLoggingIn) return
        isLoggingIn = true
        binding.loginButton.isEnabled = false

        try {
            // request: asegúrate de tener @SerializedName("EMAIL") en LoginRequest si tu backend espera mayúsculas
            val request = LoginRequest(email.trim(), password)

            // Llamada al API — puede devolver LoginResponse o Response<LoginResponse>
            val rawResponse = RetrofitInstance.api.login(request)

            // Normalizar a LoginResponse sin importar el tipo que devuelva Retrofit
            val loginResponse: com.example.tiendadecelulares.network.LoginResponse = when (rawResponse) {
                is Response<*> -> {
                    val resp = rawResponse as Response<*>
                    if (!resp.isSuccessful) {
                        // obtener errorBody para debug
                        val serverError = try { resp.errorBody()?.string() } catch (ex: Exception) { null }
                        // lanzar HttpException para que el catch superior lo maneje
                        throw HttpException(resp as Response<Any>)
                    }
                    val body = resp.body() as? com.example.tiendadecelulares.network.LoginResponse
                    body ?: throw Exception("Empty response body from server")
                }
                else -> {
                    // Retrofit may directly return LoginResponse
                    rawResponse as com.example.tiendadecelulares.network.LoginResponse
                }
            }

            // Ahora validamos el wrapper que tu backend devuelve (success/message/user)
            if (!loginResponse.success) {
                val msg = loginResponse.message ?: "Credenciales inválidas"
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                return
            }

            // Obtener user y guardar sesión (nueva firma)
            val user = loginResponse.user
            val userIdLong = user.ID.toLong()
            val userEmail = user.EMAIL
            val userIsAdmin = user.IS_ADMIN

            sessionManager.saveSession(userIdLong, userEmail, userIsAdmin)

            Toast.makeText(this@MainActivity, "Login exitoso!", Toast.LENGTH_LONG).show()

            if (callingActivity != null) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                navigateToCorrectActivity()
            }

        } catch (he: HttpException) {
            // 4xx/5xx — intenta leer errorBody si es posible
            val err = try { he.response()?.errorBody()?.string() } catch (_: Exception) { null }
            Log.e("MainActivity", "HTTP error login: code=${he.code()} body=$err")
            Toast.makeText(this@MainActivity, "Error: Credenciales incorrectas o problema del servidor.", Toast.LENGTH_LONG).show()
        } catch (ex: Exception) {
            Log.e("MainActivity", "performLogin error", ex)
            Toast.makeText(this@MainActivity, "Error de conexión: ${ex.message}", Toast.LENGTH_LONG).show()
        } finally {
            // restablecer flag y habilitar botón
            isLoggingIn = false
            binding.loginButton.isEnabled = true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REGISTER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun navigateToCorrectActivity() {
        // Aquí uso fetchUserRole() como en tu código original.
        // Asegúrate que SessionManager.fetchUserRole() devuelve "admin" o "user".
        val userRole = sessionManager.fetchUserRole()
        val intent = if (userRole == "admin") {
            Intent(this, AdminDashboardActivity::class.java)
        } else {
            Intent(this, ProductListActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}
