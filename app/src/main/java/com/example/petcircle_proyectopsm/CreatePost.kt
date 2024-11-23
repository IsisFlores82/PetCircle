package com.example.petcircle_proyectopsm

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.petcircle_proyectopsm.databinding.ActivityCreatePostBinding
import com.example.petcircle_proyectopsm.model.UserDbClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class CreatePost : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private val SELECT_IMAGE_REQUEST_CODE = 100
    private val imageUris = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinner: Spinner = binding.spinner
        val listCategories = listOf(
            "Select a category", "Anecdote", "For Fun",
            "Question", "Health", "Alimentation", "Adoption"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                val selectedItem = listCategories[position]
                Toast.makeText(this@CreatePost, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.Cancel.setOnClickListener {
            startActivity(Intent(this, Feed::class.java))
        }

        binding.submitButton.setOnClickListener {
            sendPost()
        }

        binding.AddPhoto.setOnClickListener {
            openGallery()
        }
    }

    private fun sendPost() {
        binding.submitButton.isEnabled = false

        val titleError = validTitle()
        val bodyError = validPostBody()
        val categoryError = validCategory()
        val imageError = validImage()

        if (titleError != null || bodyError != null || categoryError != null || imageError != null) {
            binding.editTextText.error = titleError
            binding.postBody.error = bodyError
            categoryError?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
            imageError?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
            binding.submitButton.isEnabled = true
            return
        }

        val title = binding.editTextText.text.toString()
        val postBody = binding.postBody.text.toString()
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val prefs = Prefs(this)
        val currentUserId = prefs.getUserId()

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: No user logged in", Toast.LENGTH_SHORT).show()
            binding.submitButton.isEnabled = true
            return
        }

        val imagesBase64 = getImagesBase64()

        val newPost = Post(
            PostId = 0,
            UserId = currentUserId,
            CategoryId = binding.spinner.selectedItemPosition,
            Title = title,
            Description = postBody,
            CreationDate = currentDate,
            UpdatedDate = currentDate,
            Status = 1,
            Images = imagesBase64
        )

        val gson = Gson()
        val jsonPost = gson.toJson(newPost)
        Log.d("Post", jsonPost)

        UserDbClient.service.savePost("Post", newPost).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                binding.submitButton.isEnabled = true
                if (response.isSuccessful) {
                    Toast.makeText(this@CreatePost, "Post creado exitosamente", Toast.LENGTH_SHORT).show()
                    limpiarFormulario()
                    startActivity(Intent(this@CreatePost, Feed::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@CreatePost,
                        "Error al crear el post: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                binding.submitButton.isEnabled = true
                Toast.makeText(this@CreatePost, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun limpiarFormulario() {
        binding.editTextText.text.clear()
        binding.postBody.text.clear()
        binding.spinner.setSelection(0)
        imageUris.clear()
    }

    private fun validImage(): String? {
        return if (imageUris.isEmpty()) {
            "Please upload at least one image"
        } else {
            null
        }
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
        return if (selectedItem == "Select a category") "Please select a valid category" else null
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                imageUris.add(it)
                displayImageThumbnail(it)
            }
        }
    }

    private fun convertUriToBase64(uri: Uri): String {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val imageBytes = outputStream.toByteArray()
            "data:image/png;base64," + Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("ImageConversion", "Error converting image to Base64", e)
            ""
        }
    }

    private fun getImagesBase64(): List<Image> {
        return imageUris.map { uri ->
            Image(
                Img = convertUriToBase64(uri) // Cambia `Img` por `Base64` si el campo correcto es Base64
            )
        }
    }

    private fun displayImageThumbnail(imageUri: Uri) {
        // Crear un nuevo ImageView con ID unico
        val imageView = ImageView(this)
        imageView.id = View.generateViewId() // Generar un ID unico
        imageView.layoutParams = LinearLayout.LayoutParams(300, 300) // tamño de las miniaturas
        imageView.setImageURI(imageUri)

        // Agregar el ImageView al LinearLayout que contiene las imágenes, usando binding
        binding.imageContainer.addView(imageView)

        // margine ntre las imagenes
        val params = imageView.layoutParams as LinearLayout.LayoutParams
        params.setMargins(16, 0, 16, 0) // margenes horizontales
        imageView.layoutParams = params
    }

}
