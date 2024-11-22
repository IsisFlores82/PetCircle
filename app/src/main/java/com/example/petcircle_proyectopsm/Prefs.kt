package com.example.petcircle_proyectopsm

import android.content.Context

class Prefs(context: Context) {

    private val SHARED_NAME = "Credentials_pref"
    private val storage = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

    // Guarda las credenciales del usuario (correo, contraseña, y ID)
    fun saveUserCredentials(email: String, password: String, userId: Int) {
        val editor = storage.edit()
        editor.putString("user_email", email)  // Guarda el correo
        editor.putString("user_password", password)  // Guarda la contraseña
        editor.putInt("user_id", userId)  // Guarda el ID del usuario
        editor.apply()
    }

    // Obtiene las credenciales del usuario (correo y contraseña)
    fun getUserCredentials(): Pair<String?, String?> {
        val email = storage.getString("user_email", null)  // Obtiene el correo
        val password = storage.getString("user_password", null)  // Obtiene la contraseña
        return Pair(email, password) // Retorna el correo y la contraseña
    }

    // Obtiene el ID del usuario
    fun getUserId(): Int {
        return storage.getInt("user_id", -1)  // Retorna el ID del usuario o -1 si no existe
    }

    // Limpia todas las credenciales guardadas
    fun clearUserCredentials() {
        val editor = storage.edit()
        editor.clear()  // Borra todos los datos guardados
        editor.apply()
    }
}
