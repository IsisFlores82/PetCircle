package com.example.petcircle_proyectopsm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import android.widget.ImageView
import android.util.Base64
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petcircle_proyectopsm.databinding.ActivityCreatePostBinding
import com.example.petcircle_proyectopsm.databinding.ActivityRegisterBinding
import com.example.petcircle_proyectopsm.model.UserDbClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody
import java.lang.Void
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.petcircle_proyectopsm.Prefs


class CreatePost : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinner: Spinner = binding.spinner
        val listCategories = listOf("Anecdote", "For Fun", "Question", "Health", "Alimentation", "Adoption")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                val selectedItem = listCategories[position]
                Toast.makeText(this@CreatePost, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        // Manejando los insets de la ventana
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.Cancel.setOnClickListener {
            val intent = Intent(this, Feed::class.java)
            startActivity(intent) // Iniciar la nueva actividad
        }

        binding.Post.setOnClickListener {
            sendPost()
            val intent = Intent(this, Feed::class.java)
            startActivity(intent) // Iniciar la nueva actividad
            Toast.makeText(this@CreatePost, "Post Created Successfully", Toast.LENGTH_SHORT).show()
        }


    }

    private fun sendPost(){
        val title = binding.editTextText.text.toString()
        val postBody = binding.postBody.text.toString()
        val category = binding.spinner.selectedItem?.toString()

        // Crear un nuevo post
            val imgBase64 = null


        val creationDate = ""
        val prefs = Prefs(this)
        val currentUserId = prefs.getUserId()

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: No user logged in", Toast.LENGTH_SHORT).show()
            return
        }


        val newPost = Post(
            PostId = 0, // Usamos 0 para indicar que será un nuevo post
            UserId = currentUserId, // El ID del usuario actual (puedes obtenerlo de tu sistema)
            CategoryId = binding.spinner.selectedItemPosition, // El ID de la categoría seleccionada
            Title = binding.editTextText.text.toString(), // Título del post
            Description = binding.postBody.text.toString(), // Descripción del post
            CreationDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()), // Fecha de creación (puedes generarla con una función)
            UpdatedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()), // Fecha de actualización (igual que la de creación inicialmente)
            Status = 1, // Estatus activo
            Img = imgBase64 // Imagen en base64 (opcional, puede ser null)

        )


        UserDbClient.service.savePost("Post", newPost).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("API", "Post guardado con éxito")
                   // Toast.makeText(this@YourActivityName, "Post creado exitosamente", Toast.LENGTH_SHORT).show()

                } else {
                    Log.e("API", "Error al guardar el post: ${response.code()}")
                   // Toast.makeText(this@YourActivityName, "Error al crear el post: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void >, t: Throwable) {
                Log.e("API", "Error de red al guardar el post: ${t.message}", t)
               // Toast.makeText(this@YourActivityName, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun validTitle(): String? {
        val title = binding.editTextText.text.toString()
        return if (title.isBlank()) "Title cannot be empty" else null
    }

    private fun validPostBody(): String? {
        val body = binding.postBody.text.toString()
        return if (body.isBlank()) "Post body cannot be empty" else null
    }

    private fun validCategory(): String? {
        val selectedItem = binding.spinner.selectedItem
        return if (selectedItem == null || selectedItem.toString().isBlank()) "Please select a category" else null
    }


}


