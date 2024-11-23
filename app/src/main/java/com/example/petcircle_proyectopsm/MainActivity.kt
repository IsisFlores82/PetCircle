package com.example.petcircle_proyectopsm

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petcircle_proyectopsm.databinding.ActivityMainBinding
import com.example.petcircle_proyectopsm.network.NetworkReceiver
import android.widget.Toast
import com.example.petcircle_proyectopsm.db.DbHelper
import com.example.petcircle_proyectopsm.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var networkReceiver: NetworkReceiver

    private lateinit var dbHelper: DbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        dbHelper = DbHelper(applicationContext)

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

        networkReceiver = NetworkReceiver {
            //Actualizar base de datos
            runOnUiThread {
                Toast.makeText(this, "Conexión restaurada", Toast.LENGTH_SHORT).show()
            }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.e("TAG", "Estado de dbHelper: $dbHelper")
                    val postRepository = PostRepository(dbHelper)
                    postRepository.synchronizePosts()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkReceiver, intentFilter)
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }
}