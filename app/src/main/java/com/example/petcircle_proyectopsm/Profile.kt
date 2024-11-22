package com.example.petcircle_proyectopsm

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petcircle_proyectopsm.databinding.ActivityLogInBinding
import com.example.petcircle_proyectopsm.databinding.ActivityProfileBinding
import com.example.petcircle_proyectopsm.model.User
import com.example.petcircle_proyectopsm.model.UserDbClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var user: User? = null

    private lateinit var imageViewProfile: ImageView
    private val SELECT_IMAGE_REQUEST_CODE = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val prefs = Prefs(this)
        val (savedEmail, savedId) = prefs.getUserData()
        if (savedId != null) {
            GetData(savedId)
        }



        binding.submitButton.setOnClickListener {
            submitForm()

            Log.d("Profile", "boton de save presionado")

        }

        imageViewProfile = binding.imageViewProfile
        val buttonSelectImage = binding.buttonSelectImage
        buttonSelectImage.setOnClickListener {
            openGallery()
        }
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

                            binding.emailEditText.setText(GettedUser.Email)
                            binding.NameEditText.setText(GettedUser.FullName)
                            binding.phoneEditText.setText(GettedUser.PhoneNumber)
                            binding.passwordEditText.setText(GettedUser.Password)
                            binding.NickNameEditText.setText(GettedUser.NickName)

                            GettedUser.Img?.let { base64 -> //checa que lo que se extrae de la api no sea null, y si no es lo manda a decodificar
                                val bitmap = decodeImage(base64)
                                if (bitmap != null) {   //aqui checa que el bitmap que se decodifico no sea null, para poder usarlo
                                    binding.imageViewProfile.setImageBitmap(bitmap)      //si sale bien lo muestra
                                } else {
                                    // y en caso de que algo falle muestra una imagen default
                                    binding.imageViewProfile.setImageResource(R.drawable.profile)
                                }
                            }
                        }

                    } else {
                        Log.d("Profile", "No se encontró el usuario")
                        Toast.makeText(this@Profile, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("Profile", "Error en la respuesta del servidor: ${response.message()}")
                    Toast.makeText(this@Profile, "Error al contactar el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("Profile", "Error de red: ${t.message}", t)
                Toast.makeText(this@Profile, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun submitForm() {
        // Validaciones de los campos
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

        if (validEmail && validPassword && validPhone && validName && validNickname) {
            // Verifica el valor de user
            Log.d("Profile", "Usuario actual antes de guardar: $user")
            if (user == null) {
                Log.e("Profile", "El usuario no está disponible")
                Toast.makeText(this, "Error: El usuario no existe", Toast.LENGTH_SHORT).show()
                return
            }

            val creationDate = user?.CreationDate.toString()
            val imgBase64 = encodeImageToBase64(binding.imageViewProfile)
            val editionDate = obtenerTimestampActual()

            // Crear editedUser
            val editedUser = User(
                UserId = user!!.UserId,
                FullName = binding.NameEditText.text.toString(),
                Password = binding.passwordEditText.text.toString(),
                PhoneNumber = binding.phoneEditText.text.toString(),
                NickName = binding.NickNameEditText.text.toString(),
                Img = imgBase64,
                Email = binding.emailEditText.text.toString(),
                Status = 1,
                CreationDate = creationDate,
                UpdatedDate = editionDate
            )

            Log.d("Profile", "Usuario a guardar: $editedUser")
            // Guardar el usuario
            UserDbClient.service.saveEditedUser("User", editedUser, editedUser.UserId)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d("API", "Usuario guardado con éxito")
                            Toast.makeText(this@Profile, "Usuario guardado correctamente", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("API", "Error al guardar el usuario: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("API", "Error en la llamada: ${t.message}")
                    }
                })
        } else {
            invalidForm()
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