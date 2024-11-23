package com.example.petcircle_proyectopsm

import PostAdapter
import PostClickedListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petcircle_proyectopsm.databinding.ActivityMyPostBinding
import com.example.petcircle_proyectopsm.model.UserDbClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPost : AppCompatActivity() {

    private var allPost = listOf<Post>() // Lista de post original sin filtrar
    private lateinit var postAdapter: PostAdapter
    private lateinit var binding: ActivityMyPostBinding
    var savedId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMyPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView con listener
//        postAdapter = PostAdapter(
//            emptyList(),
//            object : PostClickedListener {
//                override fun onPostClicked(post: Post) {
//                    navigateTo(post)
//                }
//            }
//        )

        postAdapter = PostAdapter(listOf()) { post -> navigateTo(post) }

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = postAdapter

        // Obtener el ID del usuario
        val prefs = Prefs(this)
        savedId = prefs.getUserId()
        Log.d("MyPost", "Id guardado: $savedId")

        // Llamar al API para obtener los posts
        getPostByUserId(savedId)
    }

    private fun getPostByUserId(id: Int) {
        Log.d("MyPost", "Iniciando llamada al API")
        UserDbClient.service.listPostByUserId("Post", id).enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    val postList = response.body()
                    if (postList != null && postList.isNotEmpty()) {
                        allPost = postList // Copia la lista original para filtrado
                        Log.d("MyPost", "Posts recibidos: $postList")
                        postAdapter.updatePosts(postList) // Actualiza el adapter con los posts
                    } else {
                        Log.d("MyPost", "No se encontraron posts")
                        Toast.makeText(this@MyPost, "No se encontraron posts", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("MyPost", "Error en la respuesta del servidor: ${response.message()}")
                    Toast.makeText(this@MyPost, "Error al contactar el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.e("MyPost", "Error de red: ${t.message}", t)
                Toast.makeText(this@MyPost, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateTo(post: Post) {
        val intent = Intent(this, EditPost::class.java)
        intent.putExtra(EditPost.EXTRA_POST_ID, post.PostId)
        Log.d("MyPost", "Navigating to detail with post ID: ${post.PostId}")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // recargar los posts cuando la actividad vuelva a estar en primer plano
        getPostByUserId(savedId)
    }
}
