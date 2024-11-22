package com.example.petcircle_proyectopsm.model

import com.example.petcircle_proyectopsm.Post
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ThePetCircleDbService {


    @GET("?e=Posts") // Cambia "Posts" por el controlador en tu backend, si es diferente
    fun getPosts(
        @Query("c") controller: String, // "Post" o lo que definas en tu backend
        @Query("id") id: Int = 0 // Pasa 0 para obtener todos los posts
    ): Call<List<Post>>

    @GET("?e=Users") // Este es el endpoint
    fun listUsers(@Query("c") controller: String): Call<List<User>>

    // Obtener un usuario por su ID
    @GET("?e=Users")
    fun getUserById(@Query("c") controller: String, @Query("i") userId: Int): Call<List<User>>


    @GET("?e=UserByEmail") //endpoint
    fun getUserByEmail(@Query("c") controller: String, @Query("i") email: String): Call<List<User>>

    // Crear o actualizar un usuario
    @POST("?e=Save")
    fun saveUser(
        @Query("c") controller: String,
        @Body user: User // Enviar el objeto User como JSON
    ): Call<Void>

    @POST("?e=Save")
    fun saveEditedUser(
        @Query("c") controller: String,
        @Body user: User, // Enviar el objeto User como JSON
        @Query("i") userId: Int
    ): Call<Void>

    // Eliminar un usuario.
    // Esta va extra porque no estamos haciendo eliminación directa, sino lógica, así que siempre sería con POST
    @POST("?e=Users") // Cambié DELETE por POST ya que estás usando eliminación lógica
    fun deleteUser(@Query("c") controller: String, @Query("id") userId: Int): Call<Void>
}
