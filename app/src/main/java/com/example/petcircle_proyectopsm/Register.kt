package com.example.petcircle_proyectopsm

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.petcircle_proyectopsm.databinding.ActivityRegisterBinding
import com.example.petcircle_proyectopsm.model.User
import com.example.petcircle_proyectopsm.model.UserDbClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

import kotlin.concurrent.thread

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var imageViewProfile: ImageView
    private val SELECT_IMAGE_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailFocusListener()
        passwordFocusListener()
        phoneFocusListener()
        nameFocusListener()
        nicknameFocusListener()


        binding.submitButton.setOnClickListener { submitForm() }//manda form para registro

        imageViewProfile = binding.imageViewProfile
        val buttonSelectImage = binding.buttonSelectImage
        buttonSelectImage.setOnClickListener {
            openGallery()
        }

        lifecycleScope.launch {
            try {
                val users = getUsers()
                if (users != null) {
                    Log.d("MainActivity", "Users count: ${users.size}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching users: ${e.message}")
            }
        }


    }

    private suspend fun getUsers(): List<User>? {
        return withContext(Dispatchers.IO) {
            try {
                val usersResponse = UserDbClient.service.listUsers("User")
                val body = usersResponse.execute().body()
                body
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching users: ${e.message}")
                null
            }
        }
    }


    private fun submitForm() {
        binding.emailContainer.helperText = validEmail()
        binding.passwordContainer.helperText = validPassword()
        binding.phoneContainer.helperText = validPhone()
        binding.NameContainer.helperText = validName()
        binding.NickNameContainer.helperText = validNickname()

        val validEmail = binding.emailContainer.helperText == null
        val validPassword = binding.passwordContainer.helperText == null
        val validPhone = binding.phoneContainer.helperText == null
        val validName = binding.NameContainer.helperText == null
        val validNickname = binding.NickNameContainer.helperText == null

        if (validEmail && validPassword && validPhone && validName && validNickname){

            val creationDate = obtenerTimestampActual()
            val imgBase64 = encodeImageToBase64(binding.imageViewProfile)

            // Crear un usuario
            val newUser = User(
                UserId = 0, // Usamos 0 para crear uno nuevo
                FullName = binding.NameEditText.text.toString(),
                Password = binding.passwordEditText.text.toString(),
                PhoneNumber = binding.phoneEditText.text.toString(),
                NickName = binding.NickNameEditText.text.toString(),
                Img = imgBase64,
                Email = binding.emailEditText.text.toString(),
                Status = 1,
                CreationDate = creationDate,
                UpdatedDate = creationDate
            )

            Log.d("API", "Usuario a guardar: $newUser")

            UserDbClient.service.saveUser("User", newUser).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("API", "Usuario guardado con éxito")
                        resetForm()
                    } else {
                        Log.e("API", "Error al guardar el usuario: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("API", "Error en la llamada: ${t.message}")
                }
            })

        }
        else{
            invalidForm()
        }

        //
        //else
        //invalidForm()
    }

    private fun invalidForm() {
        var message = ""
        if (binding.emailContainer.helperText != null)
            message += "\n\nEmail: " + binding.emailContainer.helperText
        if (binding.passwordContainer.helperText != null)
            message += "\n\nPassword: " + binding.passwordContainer.helperText
        if (binding.phoneContainer.helperText != null)
            message += "\n\nPhone: " + binding.phoneContainer.helperText
        if (binding.NameContainer.helperText != null)
            message += "\n\nName: " + binding.NameContainer.helperText
        if (binding.NickNameContainer.helperText != null)
            message += "\n\nNickname: " + binding.NickNameContainer.helperText

        AlertDialog.Builder(this)
            .setTitle("Invalid Form")
            .setMessage(message)
            .setPositiveButton("Okay") { _, _ ->
                // do nothing
            }
            .show()
    }

    private fun resetForm() {
        var message = "Email: " + binding.emailEditText.text
        message += "\nPassword: " + binding.passwordEditText.text
        message += "\nPhone: " + binding.phoneEditText.text
        message += "\nName: " + binding.NameEditText.text
        message += "\nName: " + binding.NickNameEditText.text
        AlertDialog.Builder(this)
            .setTitle("Form submitted")
            .setMessage(message)
            .setPositiveButton("Okay") { _, _ ->
                binding.emailEditText.text = null
                binding.passwordEditText.text = null
                binding.phoneEditText.text = null
                binding.NameEditText.text = null
                binding.NickNameEditText.text = null

                binding.emailContainer.helperText = getString(R.string.required)
                binding.passwordContainer.helperText = getString(R.string.required)
                binding.phoneContainer.helperText = getString(R.string.required)
                binding.NameContainer.helperText = getString(R.string.required)
            }
            .show()
    }

    private fun emailFocusListener() {
        binding.emailEditText.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.emailContainer.helperText = validEmail()
            }
        }
    }

    private fun validEmail(): String? {
        val emailText = binding.emailEditText.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid Email Address"
        }
        return null
    }

    private fun passwordFocusListener() {
        binding.passwordEditText.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.passwordContainer.helperText = validPassword()
            }
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.passwordEditText.text.toString()
        if (passwordText.length < 8) {
            return "Minimum 8 Character Password"
        }
        if (!passwordText.matches(".*[A-Z].*".toRegex())) {
            return "Must Contain 1 Upper-case Character"
        }
        if (!passwordText.matches(".*[a-z].*".toRegex())) {
            return "Must Contain 1 Lower-case Character"
        }
        if (!passwordText.matches(".*[@#\$%^&+=.].*".toRegex())) {
            return "Must Contain 1 Special Character (@#\$%^&+=)"
        }

        return null
    }

    private fun phoneFocusListener() {
        binding.phoneEditText.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.phoneContainer.helperText = validPhone()
            }
        }
    }

    private fun validPhone(): String? {
        val phoneText = binding.phoneEditText.text.toString()
        if (!phoneText.matches(".*[0-9].*".toRegex())) {
            return "Must be all Digits"
        }
        if (phoneText.length != 10) {
            return "Must be 10 Digits"
        }
        return null
    }

    private fun nameFocusListener() {
        binding.NameEditText.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.NameContainer.helperText = validName()
            }
        }
    }

    private fun validName(): String? {
        val nameText = binding.NameEditText.text.toString()

        if (nameText.length < 3) {
            return "Minimum 3 Characters Name"
        }
        if (nameText.length > 30) {
            return "Maximun 30 Characters Name"
        }
        return null
    }

    private fun nicknameFocusListener() {
        binding.NickNameEditText.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.NickNameContainer.helperText = validNickname()
            }
        }
    }

    private fun validNickname(): String? {
        val nicknameText = binding.NickNameEditText.text.toString()

        if (nicknameText.length < 3) {
            return "Minimum 3 Characters Nickname"
        }
        if (nicknameText.length > 30) {
            return "Maximum 30 Characters Nickname"
        }
        return null
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
                imageViewProfile.setImageURI(it)  // Muestra la imagen seleccionada
            }
        }
    }


    fun obtenerTimestampActual(): String {
        val ahora = LocalDateTime.now() // Fecha y hora actuales
        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // Formato tipo SQL
        return ahora.format(formato) // Devuelve el timestamp como String
    }

    fun encodeImageToBase64(imageView: ImageView): String {
        imageView.isDrawingCacheEnabled = true
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val imageBytes = outputStream.toByteArray()
        return "data:image/png;base64," + Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

}