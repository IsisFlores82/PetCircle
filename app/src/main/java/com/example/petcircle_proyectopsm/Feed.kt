package com.example.petcircle_proyectopsm

import PostAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcircle_proyectopsm.databinding.ActivityFeedBinding
import com.example.petcircle_proyectopsm.model.UserDbClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Feed : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding
        val binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView
        postAdapter = PostAdapter(listOf())
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = postAdapter

        // Configurar el menú lateral (Drawer)
        setupDrawer(binding)

        // Llenar el feed con datos de la API
        fetchPosts(binding)
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

    private fun fetchPosts(binding: ActivityFeedBinding) {
        // Llamada a la API para obtener los posts
        UserDbClient.service.getPosts("Post", 0).enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    val posts = response.body() ?: emptyList()
                    Log.d("Feed", "Posts recibidos: $posts")
                    postAdapter = PostAdapter(posts)
                    binding.recycler.adapter = postAdapter
                } else {
                    Log.e("Feed", "Error en la respuesta: ${response.code()} - ${response.errorBody()?.string()}")
                }
                Log.d("Feed", "Response body: ${response.body()}")

            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.e("Feed", "Error al obtener los posts: ${t.message}")
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
}
