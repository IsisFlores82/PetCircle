package com.example.petcircle_proyectopsm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petcircle_proyectopsm.databinding.ActivityCreatePostBinding

class CreatePost : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityCreatePostBinding.inflate(layoutInflater)
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
            val intent = Intent(this, Feed::class.java)
            startActivity(intent) // Iniciar la nueva actividad
            Toast.makeText(this@CreatePost, "Post Created Successfully", Toast.LENGTH_SHORT).show()
        }


    }
}
