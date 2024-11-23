package com.example.petcircle_proyectopsm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petcircle_proyectopsm.databinding.ActivityEditPostBinding
import com.example.petcircle_proyectopsm.databinding.ActivityProfileBinding
import com.example.petcircle_proyectopsm.model.User
import com.example.petcircle_proyectopsm.model.UserDbClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Base64

class EditPost : AppCompatActivity() {

    companion object{
        const val EXTRA_POST_ID = "post_id"
    }

    private lateinit var binding: ActivityEditPostBinding
    var postId: Int = 0
    private var post: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        postId = intent.getIntExtra(EXTRA_POST_ID, -1)
        Log.d("EditPost", "postId: $postId")

        if (postId != -1) {
            GetPost(postId)
        } else {
            Toast.makeText(this, "Error al obtener el ID del post", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.buttonSave.setOnClickListener {
            updatePost()
        }

        binding.buttonDelete.setOnClickListener {
            DeletePost()
        }

    }


    private fun GetPost(id: Int) {
        UserDbClient.service.getPostById("Post", id).enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    val postList = response.body()
                    if (postList != null && postList.isNotEmpty()) {
                        post = postList[0]
                        val currentPost = post

                        if (currentPost != null) {
                            binding.PostTitle.setText(currentPost.Title)
                            binding.category.text = currentPost.CategoryName
                            binding.data.text = currentPost.CreationDate
                            binding.PostBody.setText(currentPost.Description)

                            // Configurar el ViewPager2
                            val images = currentPost.Images.map { it.Img } // Extraer imágenes
                            val imageAdapter = ImageAdapter(images)
                            binding.imageContainer.adapter = imageAdapter
                        }
                    } else {
                        Toast.makeText(this@EditPost, "No se encontró el post", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EditPost, "Error al contactar el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(this@EditPost, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun DeletePost() {
        // Crear la solicitud con el estado de baja lógica (Status = 0)
        val updatedStatus = UpdateStatusRequest(
            PostId = postId,
            Status = 0 // Usamos 0 para indicar baja lógica
        )

        // Hacer la solicitud a la API
        UserDbClient.service.deletePost("Post", updatedStatus).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {

                    Toast.makeText(this@EditPost, "Post  eliminado", Toast.LENGTH_SHORT).show()
                    finish() // cerrar la actividad
                } else {
                    // Manejo de error si la respuesta no es exitosa
                    Toast.makeText(this@EditPost, "Error al eliminar el post", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Manejo de fallo en la conexión
                Toast.makeText(this@EditPost, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePost() {
        // Obtener los valores de los campos
        val title = binding.PostTitle.text.toString().trim()
        val description = binding.PostBody.text.toString().trim()

        // Validación para asegurarse de que los campos no estén vacíos
        if (title.isEmpty()) {
            Toast.makeText(this@EditPost, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
            return // No continuar si el título está vacío
        }

        if (description.isEmpty()) {
            Toast.makeText(this@EditPost, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show()
            return // No continuar si la descripción está vacía
        }

        // Crear la solicitud con los datos actualizados
        val updatePostInfoRequest = UpdatePostInfoRequest(
            PostId = postId,
            Title = title,
            Description = description
        )

        // Hacer la solicitud a la API
        UserDbClient.service.editPost("Post", updatePostInfoRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditPost, "Post actualizado", Toast.LENGTH_SHORT).show()
                    finish() // Cerrar la actividad
                } else {
                    // Manejo de error si la respuesta no es exitosa
                    Toast.makeText(this@EditPost, "Error al actualizar el post", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Manejo de fallo en la conexión
                Toast.makeText(this@EditPost, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun decodeImage(base64: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

}