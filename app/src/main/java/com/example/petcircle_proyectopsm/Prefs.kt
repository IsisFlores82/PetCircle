package com.example.petcircle_proyectopsm

import android.content.Context

class Prefs(val context: Context) {
    val SHARED_NAME = "Credentials_pref"
    private val storage = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

<<<<<<< HEAD
    // Guarda las credenciales del usuario
    fun saveUserCredentials(email: String, password: String) {
        val editor = storage.edit()
        editor.putString("user_email", email)  // Guarda el correo
        editor.putString("user_password", password)  // Guarda la contraseña
=======
    fun saveUserCredentials(email: String, password: String, id: Int) {
        val editor = storage.edit()
        editor.putString("user_email", email)  // gruarda el correo
        editor.putString("user_password", password)  // guarda la pass
        editor.putString("user_id", password)
>>>>>>> e386c27f883cde6aaa97e9dbfc0be6a049c7563c
        editor.apply()
    }

    // Obtiene las credenciales del usuario
    fun getUserCredentials(): Pair<String?, String?> {
        val email = storage.getString("user_email", null)  // Obtiene el correo
        val password = storage.getString("user_password", null)  // Obtiene la contraseña
        return Pair(email, password) // Retorna el correo y la contraseña
    }

<<<<<<< HEAD
    // Limpia las credenciales del usuario
=======
    fun getUserData(): Pair<String?, Int?> {
        val email = storage.getString("user_email", null)  // obtiene el correo
        val id = storage.getInt("user_id", 0)  // obtiene el id
        return Pair(email, id) //las regresa
    }

>>>>>>> e386c27f883cde6aaa97e9dbfc0be6a049c7563c
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

