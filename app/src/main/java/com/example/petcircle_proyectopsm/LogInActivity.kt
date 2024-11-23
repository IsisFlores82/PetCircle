package com.example.petcircle_proyectopsm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.petcircle_proyectopsm.databinding.ActivityLogInBinding
import com.example.petcircle_proyectopsm.db.DbHelper
import com.example.petcircle_proyectopsm.model.User
import com.example.petcircle_proyectopsm.model.UserDbClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding
    private var user: User? = null

    companion object{
        lateinit var dbHelper: DbHelper
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // checa si hay credenciales guardadas
        val prefs = Prefs(this) //instancia la clase con el conetexto de la ventana
        val (savedEmail, savedPassword) = prefs.getUserCredentials()
        if (savedEmail != null && savedPassword != null) {
            // Si hay credenciales, intenta iniciar sesión automáticamente
            loginUserByEmail(savedEmail, savedPassword)
        }

        // Configurar el listener del botón de inicio de sesión
        binding.submitButton.setOnClickListener {
            Log.d("LogInActivity", "boton de inicio de sesion presionado")
            val email = binding.emailEditText.text.toString()
            val password = binding.PasswordEditText.text.toString()

            loginUserByEmail(email, password)
        }
    }

    private fun loginUserByEmail(email: String, password: String) {
        Log.d("LogInActivity", "Iniciando llamada al API")
        UserDbClient.service.getUserByEmail("User", email).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val userList = response.body()
                    if (userList != null && userList.isNotEmpty()) {
                        val user = userList[0] // Se toma el primer usuario de la lista
                        Log.d("LogInActivity", "Usuario encontrado: ${user.FullName}")

                        if (user.Password == password) {
                            // guarda las credenciales en SharedPreferences
                            val prefs = Prefs(this@LogInActivity)

                           // prefs.saveUserCredentials(email, password)
                            prefs.saveUserCredentials(email, password, user.UserId, user.Img, user.PhoneNumber, user.FullName, user.NickName)
                            prefs.saveUserCredentials(email, password, user.UserId, user.Img, user.PhoneNumber, user.FullName, user.NickName)

                            dbHelper = DbHelper(applicationContext)

                            dbHelper.insertUser(user)

                            // te manda a la ventana del feed
                            val intent = Intent(this@LogInActivity, Feed::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@LogInActivity, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d("LogInActivity", "No se encontró el usuario")
                        Toast.makeText(this@LogInActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("LogInActivity", "Error en la respuesta del servidor: ${response.message()}")
                    Toast.makeText(this@LogInActivity, "Error al contactar el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("LogInActivity", "Error de red: ${t.message}", t)
                Toast.makeText(this@LogInActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
