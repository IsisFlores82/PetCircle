package com.example.petcircle_proyectopsm

import android.content.Context
import android.content.SharedPreferences

class Prefs(val context: Context) {
    val SHARED_NAME = "Credentials_pref"
    private val storage = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

    fun saveUserCredentials(email: String, password: String) {
        val editor = storage.edit()
        editor.putString("user_email", email)  // gruarda el correo
        editor.putString("user_password", password)  // guarda la pass
        editor.apply()
    }

    fun getUserCredentials(): Pair<String?, String?> {
        val email = storage.getString("user_email", null)  // obtiene el correo
        val password = storage.getString("user_password", null)  // obtiene la contraseña
        return Pair(email, password) //las regresa
    }

    fun clearUserCredentials() {
        val editor = storage.edit()
        editor.clear()  // borra todos los datos guardados
        editor.apply()
    }
}