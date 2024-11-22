package com.example.petcircle_proyectopsm

import android.content.Context

class Prefs(val context: Context) {
    val SHARED_NAME = "Credentials_pref"
    private val storage = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

    // Guarda las credenciales del usuario
    fun saveUserCredentials(email: String, password: String) {
        val editor = storage.edit()
        editor.putString("user_email", email)  // Guarda el correo
        editor.putString("user_password", password)  // Guarda la contraseña
        editor.apply()
    }

    // Obtiene las credenciales del usuario
    fun getUserCredentials(): Pair<String?, String?> {
        val email = storage.getString("user_email", null)  // Obtiene el correo
        val password = storage.getString("user_password", null)  // Obtiene la contraseña
        return Pair(email, password) // Retorna el correo y la contraseña
    }

    // Limpia las credenciales del usuario
    fun clearUserCredentials() {
        val editor = storage.edit()
        editor.clear()  // Borra todos los datos guardados
        editor.apply()
    }

    // Guarda el UserId
    fun saveUserId(userId: Int) {
        val editor = storage.edit()
        editor.putInt("user_id", userId)  // Guarda el ID del usuario
        editor.apply()
    }

    // Obtiene el UserId
    fun getUserId(): Int {
        return storage.getInt("user_id", -1)  // Retorna el ID del usuario o -1 si no existe
    }
}

