package com.example.petcircle_proyectopsm

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petcircle_proyectopsm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.btnLogIn.setOnClickListener {

            // checa si hay credenciales guardadas
            val prefs = Prefs(this) //instancia la clase con el conetexto de la ventana
            val (savedEmail, savedPassword) = prefs.getUserCredentials()

            if (savedEmail != null && savedPassword != null) {
                // Si hay credenciales, intenta va directo al feed
                val intent = Intent(this@MainActivity, Feed::class.java)
                startActivity(intent)
            }
            else {
                // Si no hay credenciales, lleva a la ventana de inicio de sesión
                val intent = Intent(this, LogInActivity::class.java)
                startActivity(intent)
            }

        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent) // Iniciar la nueva actividad
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}