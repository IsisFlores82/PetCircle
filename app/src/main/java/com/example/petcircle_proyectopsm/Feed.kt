package com.example.petcircle_proyectopsm

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petcircle_proyectopsm.databinding.ActivityFeedBinding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import android.view.MenuItem
import android.widget.Toast
import android.util.Log


class Feed : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding
        val binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.adapter = PostAdapter(
            listOf(
                Post("She happy?", "habia una fiesta y ella tambien estaba feliz!", "https://picsum.photos/200/300"),
                Post("otro post", "wuaw wuaw!", "https://picsum.photos/200/300"),
                Post("mm tengo sed", "glup glup", "https://picsum.photos/200/300"),
                Post("She happy?", "habia una fiesta y ella tambien estaba feliz!", "https://picsum.photos/200/300"),
            )
        )


        drawerLayout = binding.drawerLayout
        navigationView = binding.navView
        toolbar = binding.myToolbar

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navDraweOpen, R.string.navDraweClose
        )
        drawerLayout.addDrawerListener(toggle)
        //toolbar.setBackgroundColor(Color.TRANSPARENT)
        //toggle.setHomeAsUpIndicator(Color.TRANSPARENT)
        toggle.isDrawerIndicatorEnabled = true

        // cambia el icono del menu de hamburguesa
        toggle.setHomeAsUpIndicator(R.drawable.profile)

        toggle.syncState()

        // Maneja el botón de navegación de la barra de acción
       //supportActionBar?.setDisplayHomeAsUpEnabled(true)
       //supportActionBar?.setHomeButtonEnabled(true)

        // Manejar el evento de clic en el ícono
        //toggle.setToolbarNavigationClickListener {
        //    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
        //        drawerLayout.closeDrawer(GravityCompat.START)
        //    } else {
        //        drawerLayout.openDrawer(GravityCompat.START)
        //    }
        //}

        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            // Maneja el clic en los elementos del menú
            when (menuItem.itemId) {
                R.id.forFun -> {
                    Log.d("MenuClick", "Item clicked: For Fun") // Agrega el log aquí
                    Toast.makeText(this, "for fun clickeado", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.questions -> {
                    Log.d("MenuClick", "Item clicked: Questions") // Agrega el log aquí
                    Toast.makeText(this, "questions clickeado", Toast.LENGTH_SHORT).show()
                    true
                }
                // Agrega más casos según tus elementos de menú
                else -> false
            }.also {
                // Siempre cierra el menú al seleccionar un elemento
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Permite que el toggle del Drawer funcione
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }


}
