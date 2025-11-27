package com.example.tiendadecelulares.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestiona la sesión del usuario guardando y recuperando datos en SharedPreferences.
 */
class SessionManager(context: Context) {

    private var prefs: SharedPreferences = context.getSharedPreferences("TiendaAppPrefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_ID = "user_id"
        const val AUTH_TOKEN = "auth_token"
        const val USER_ROLE = "user_role"
    }

    /**
     * Guarda los datos de la sesión del usuario.
     */
    fun saveSession(userId: Long, email: String, isAdmin: Int) {
        val editor = prefs.edit()
        editor.putLong("USER_ID", userId)
        editor.putString("USER_EMAIL", email)
        val role = if (isAdmin == 1) "admin" else "user"
        editor.putString(USER_ROLE, role)
        editor.apply()
    }


    /**
     * Recupera el token de autenticación del usuario.
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    /**
     * Recupera el rol del usuario.
     */
    fun fetchUserRole(): String? {
        return prefs.getString(USER_ROLE, null)
    }

    /**
     * Recupera el ID del usuario.
     */
    fun fetchUserId(): Long {
        return prefs.getLong(USER_ID, -1)
    }

    /**
     * Limpia los datos de la sesión (logout).
     */
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
