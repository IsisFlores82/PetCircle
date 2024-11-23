package com.example.petcircle_proyectopsm

import PostAdapter
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcircle_proyectopsm.databinding.ActivityFeedBinding
import com.example.petcircle_proyectopsm.db.DbHelper
import com.example.petcircle_proyectopsm.model.User
import com.example.petcircle_proyectopsm.model.UserDbClient
import com.example.petcircle_proyectopsm.network.NetworkReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private lateinit var networkReceiver: NetworkReceiver
    private var user: User? = null
    private var allPost = listOf<Post>() // Lista de post original sin filtrar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView
        postAdapter = PostAdapter(listOf()) { post -> onPostClicked(post) }
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = postAdapter

        // Configurar el menú lateral (Drawer)
        setupDrawer(binding)

        //carga los datos de las shared preferences de base
        GetDataSP()
        val prefs = Prefs(this)
        val savedId = prefs.getUserId()

        // si se quiesieran manda a cargar los datos de la interfaz con la api se usa esa funcion:
         //GetData(savedId)


        //carga los post de la api
        getPost()

        //para ocultar y desoculatar la barra delbuscador y el nombre de la app
        binding.toggleButton.setOnClickListener {
            if (binding.textView.visibility == View.GONE) {
                // Oculta el TextView y muestra el EditText
                binding.textView.visibility = View.VISIBLE
                binding.etSearch.visibility = View.GONE

                //cambia de color la lupa
                binding.toggleButton.clearColorFilter()
            } else {
                // Muestra el TextView y oculta el EditText
                binding.textView.visibility = View.GONE
                binding.etSearch.visibility = View.VISIBLE

                //cambia de color la lupa
                binding.toggleButton.setColorFilter(ContextCompat.getColor(this, R.color.brown), android.graphics.PorterDuff.Mode.SRC_IN)
            }
        }

        networkReceiver = NetworkReceiver {
            //Actualizar base de datos
            runOnUiThread {
                Toast.makeText(this, "Conexión restaurada", Toast.LENGTH_SHORT).show()
            }
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // cuando tiene internet



                    // Sincronizar datos entre SQLite y MySQL
                    //sincronizarDatosConServidor()
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Manejo de errores, si es necesario
                }
            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                filtrar(p0.toString())
            }

        })

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
                R.id.New -> {
                    Log.d("MenuClick", "Item clicked: Whats new")
                    getPost()
                    Toast.makeText(this, "Whats new clicker", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.forFun -> {
                    Log.d("MenuClick", "Item clicked: For Fun")
                    getPostByCategory(2)
                    Toast.makeText(this, "For Fun clicker", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.questions -> {
                    Log.d("MenuClick", "Item clicked: Questions")
                    getPostByCategory(3)
                    Toast.makeText(this, "Questions clicker", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.Anecdote -> {
                    Log.d("MenuClick", "Item clicked: Anecdote")
                    getPostByCategory(1)
                    Toast.makeText(this, "Anecdote clicker", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.Medic -> {
                    Log.d("MenuClick", "Item clicked: Health")
                    getPostByCategory(4)
                    Toast.makeText(this, "Medic clicker", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.Aliment -> {
                    Log.d("MenuClick", "Item clicked: Aliment")
                    getPostByCategory(5)
                    Toast.makeText(this, "Aliment clicker", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.Adption -> {
                    Log.d("MenuClick", "Item clicked: Adoption")
                    getPostByCategory(6)
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
                R.id.mypost -> {
                    Log.d("MenuClick", "Item clicked: My posts")
                    Toast.makeText(this, "My posts clickeado", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MyPost::class.java)
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
                        allPost = postList // saca la copia para poder filtrar
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

    private fun getPostByCategory(id: Int) {
        Log.d("Feed", "Iniciando llamada al API")
        UserDbClient.service.listPostByCategory("Post", id).enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    val postList = response.body()
                    if (postList != null && postList.isNotEmpty()) {
                        allPost = postList // saca la copia para poder filtrar
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
                        val user = userList[0] // Se toma el primer usuario de la lista

                        user.Img?.let { base64 ->
                            val bitmap = decodeImage(base64)
                            if (bitmap != null) {
                                // pone la fto de perfil de la barra superior
                                binding.imgProfile.setImageBitmap(bitmap)


                                // Configurar la imagen de perfil en el header
                                val headerView = navigationView.getHeaderView(0)
                                val profileImageView = headerView.findViewById<ImageView>(R.id.MenuProfileImg)
                                val usernameTextView = headerView.findViewById<TextView>(R.id.MenuName)
                                val nickTextView = headerView.findViewById<TextView>(R.id.MenuNick)

                                profileImageView.setImageBitmap(bitmap) // Cambia la imagen de perfil
                                usernameTextView.text = user.FullName // Cambia el nombre del usuario
                                nickTextView.text = user.NickName // Cambia el nickname
                            } else {
                                // Si no hay imagen válida, muestra una imagen por defecto
                                binding.imgProfile.setImageResource(R.drawable.profile)
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


    private fun GetDataSP() {
        val prefs = Prefs(this) // Instancia la clase con el contexto de la ventana

        // Obtener las credenciales de usuario completas desde SharedPreferences
        val userCredentials = prefs.getAllUserCredentials()
        if (userCredentials != null) {

            // Decodificar la imagen de perfil desde base64 (si está guardada)
            val bitmap = if (userCredentials.userPhoto.isNotEmpty()) {
                decodeImage(userCredentials.userPhoto)
            } else {
                null
            }

            // toma los contenedores del header del menu
            val headerView = navigationView.getHeaderView(0)
            val profileImageView = headerView.findViewById<ImageView>(R.id.MenuProfileImg)
            val usernameTextView = headerView.findViewById<TextView>(R.id.MenuName)
            val nickTextView = headerView.findViewById<TextView>(R.id.MenuNick)

            if (bitmap != null) {
                profileImageView.setImageBitmap(bitmap) // Cambia la imagen de perfil del header
                binding.imgProfile.setImageBitmap(bitmap) //de la top bar
            }

            //le asigna los datos del usuario
            usernameTextView.text = userCredentials.userName // Cambia el nombre del usuario
            nickTextView.text = userCredentials.userNick // Cambia el nickname
        } else {
            // Manejo de caso donde no hay datos en las SharedPreferences
            Toast.makeText(this, "No se encontraron credenciales guardadas.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun decodeImage(base64String: String): Bitmap? {
        // remueve el "data:image/png;base64," de la cadena en caso de estar, poque no se necesita para decodificacion
        val cleanBase64 = base64String.replace("data:image/png;base64,", "")

        //esto retorna un array de bytes ya decodificados
        val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

        //el array de bytes se convierte en un bitmap que se puede mostrar en el RV
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

    }


    private fun filtrar(texto: String) {

        if (allPost.isEmpty()) return // por si por algun motivo no se ha cargado la lista de albums

        val postFiltrados = allPost.filter {
            it.Title.contains(texto, ignoreCase = true)
        }
        postAdapter.filtrar(postFiltrados)

        //la funcion de filter es mejor que esta otra que es mas "manual" ----
        //var albumsFiltrados = listOf<Album>()
        //allAlbums.forEach{
        //    if (it.strTitle.toLowerCase().contains(texto.toLowerCase())) {
        //        albumsFiltrados += it
        //    }
        //}
        //postAdapter.filtrar(albumsFiltrados)
        ////postAdapter.notifyDataSetChanged()
    }

    private fun onPostClicked(post: Post) {
        Toast.makeText(this, "Post seleccionado: ${post.Title}", Toast.LENGTH_SHORT).show()
    }

}
