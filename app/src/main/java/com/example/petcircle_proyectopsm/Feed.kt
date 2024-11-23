package com.example.petcircle_proyectopsm

import PostAdapter
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcircle_proyectopsm.databinding.ActivityFeedBinding
import com.example.petcircle_proyectopsm.db.DbHelper
import com.example.petcircle_proyectopsm.model.User
import com.example.petcircle_proyectopsm.model.UserDbClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class Feed : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var postAdapter: PostAdapter
    private lateinit var binding: ActivityFeedBinding
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView
        postAdapter = PostAdapter(listOf())
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = postAdapter

        // Configurar el menú lateral (Drawer)
        setupDrawer(binding)

        val prefs = Prefs(this)
        val savedId = prefs.getUserId()

        // checa q el id no sea invalido
        if (savedId != -1) {
            GetData(savedId)
        } else {
            Toast.makeText(this, "No se encontró al usuario", Toast.LENGTH_SHORT).show()
        }



        // Llenar el feed con datos de la API
        getPost()
    }

    private fun setupDrawer(binding: ActivityFeedBinding) {
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView
        toolbar = binding.myToolbar

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navDraweOpen, R.string.navDraweClose
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        navigationView.bringToFront()

        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.forFun -> {
                    Log.d("MenuClick", "Item clicked: For Fun")
                    Toast.makeText(this, "For Fun clicker", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.questions -> {
                    Log.d("MenuClick", "Item clicked: Questions")
                    Toast.makeText(this, "Questions clicker", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.Anecdote -> {
                    Log.d("MenuClick", "Item clicked: Anecdote")
                    Toast.makeText(this, "Anecdote clicker", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.Medic -> {
                    Log.d("MenuClick", "Item clicked: Medic")
                    Toast.makeText(this, "Medic clicker", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.Aliment -> {
                    Log.d("MenuClick", "Item clicked: Aliment")
                    Toast.makeText(this, "Aliment clicker", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.Adption -> {
                    Log.d("MenuClick", "Item clicked: Adoption")
                    Toast.makeText(this, "Adoption clickeado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.profile -> {
                    Log.d("MenuClick", "Item clicked: Profile")
                    Toast.makeText(this, "Profile clickeado", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                    true
                }
                R.id.LogOut -> {
                    Log.d("MenuClick", "Item clicked: Cerrar Sesión")
                    Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()

                    val prefs = Prefs(this)
                    prefs.clearUserCredentials()


          LogInActivity.dbHelper.onLogOut()

                    // Redirigir a MainActivity y limpiar la pila de actividades


                    LogInActivity.dbHelper.onLogOut()

                    // Redirigir a MainActivity y limpiar la pila de actividades


                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }.also {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

        binding.createPost.setOnClickListener {
            startActivity(Intent(this, CreatePost::class.java))
        }
    }


    private fun getPost() {
        Log.d("Feed", "Iniciando llamada al API")
        UserDbClient.service.listPost("Post").enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    val postList = response.body()
                    if (postList != null && postList.isNotEmpty()) {

                        Log.d("Feed", "Posts recibidos: $postList")
                        postAdapter.updatePosts(postList) // pasarle los posts al adapter

                    } else {
                        Log.d("Feed", "No se encontraron post")
                        Toast.makeText(this@Feed, "post n oencontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("Feed", "Error en la respuesta del servidor: ${response.message()}")
                    Toast.makeText(this@Feed, "Error al contactar el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.e("LogInActivity", "Error de red: ${t.message}", t)
                Toast.makeText(this@Feed, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }


    private fun GetData(id: Int) {
        Log.d("Profile", "Iniciando llamada al API")
        UserDbClient.service.getUserById("User", id).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val userList = response.body()
                    if (userList != null && userList.isNotEmpty()) {
                        user = userList[0] // Se toma el primer usuario de la lista
                        val GettedUser = user
                        if (GettedUser != null) {
                            Log.d("Profile", "Usuario encontrado: ${GettedUser.FullName}")


                            GettedUser.Img?.let { base64 -> //checa que lo que se extrae de la api no sea null, y si no es lo manda a decodificar
                                val bitmap = decodeImage(base64)
                                if (bitmap != null) {   //aqui checa que el bitmap que se decodifico no sea null, para poder usarlo
                                    binding.imgProfile.setImageBitmap(bitmap)      //si sale bien lo muestra
                                } else {
                                    // y en caso de que algo falle muestra una imagen default
                                    binding.imgProfile.setImageResource(R.drawable.profile)
                                }
                            }
                        }

                    } else {
                        Log.d("Profile", "No se encontró el usuario")
                        Toast.makeText(this@Feed, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("Profile", "Error en la respuesta del servidor: ${response.message()}")
                    Toast.makeText(this@Feed, "Error al contactar el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("Profile", "Error de red: ${t.message}", t)
                Toast.makeText(this@Feed, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun decodeImage(base64String: String): Bitmap? {
        // remueve el "data:image/png;base64," de la cadena en caso de estar, poque no se necesita para decodificacion
        val cleanBase64 = base64String.replace("data:image/png;base64,", "")

        //esto retorna un array de bytes ya decodificados
        val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

        //el array de bytes se convierte en un bitmap que se puede mostrar en el RV
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

    }

}
