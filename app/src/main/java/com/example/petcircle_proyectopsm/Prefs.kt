package com.example.petcircle_proyectopsm

import android.content.Context

class Prefs(context: Context) {

    private val SHARED_NAME = "Credentials_pref"
    private val storage = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

    // Data class para encapsular las credenciales completas del usuario
    data class UserCredentials(
        val email: String,
        val password: String,
        val userId: Int,
        val userPhoto: String,
        val userPhone: String,
        val userName: String,
        val userNick: String
    )

    // Guarda las credenciales del usuario
    fun saveUserCredentials(
        email: String,
        password: String,
        userId: Int,
        userPhoto: String,
        userPhone: String,
        userName: String,
        userNick: String
    ) {
        val editor = storage.edit()
        editor.putString("user_email", email)
        editor.putString("user_password", password)
        editor.putInt("user_id", userId)
        editor.putString("user_photo", userPhoto)
        editor.putString("user_phone", userPhone)
        editor.putString("user_name", userName)
        editor.putString("user_nick", userNick)

        editor.apply()
    }

    // Obtiene solo el correo y la contraseña del usuario
    fun getUserCredentials(): Pair<String?, String?> {
        val email = storage.getString("user_email", null)
        val password = storage.getString("user_password", null)
        return Pair(email, password)
    }

    // Obtiene todas las credenciales como un objeto UserCredentials
    fun getAllUserCredentials(): UserCredentials? {
        val email = storage.getString("user_email", null)
        val password = storage.getString("user_password", null)
        val userId = storage.getInt("user_id", -1)
        val userPhoto = storage.getString("user_photo", null)
        val userPhone = storage.getString("user_phone", null)
        val userName = storage.getString("user_name", null)
        val userNick = storage.getString("user_nick", null)

        return if (email != null && password != null && userId != -1) {
            UserCredentials(
                email = email,
                password = password,
                userId = userId,
                userPhoto = userPhoto ?: "",
                userPhone = userPhone ?: "",
                userName = userName ?: "",
                userNick = userNick ?: ""
            )
        } else {
            null
        }
    }

    fun getUserId(): Int {
        return storage.getInt("user_id", -1) // Retorna el ID del usuario o -1 si no existe
    }

    // Limpia todas las credenciales guardadas
    fun clearUserCredentials() {
        val editor = storage.edit()
        editor.clear()
        editor.apply()
    }
}
