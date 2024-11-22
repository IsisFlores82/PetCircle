package com.example.petcircle_proyectopsm
data class Post(
    val PostId: Int,
    val UserId: Int,
    val CategoryId: Int,
    val Title: String,
    val Description: String,
    val CreationDate: String,
    val UpdatedDate: String,
    val Status: Int,
    val Img: String? = null // Si hay imágenes
)
