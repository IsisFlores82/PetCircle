package com.example.petcircle_proyectopsm
data class Post(
    val PostId: Int,
    val UserId: Int,
    val CategoryId: Int?,
    val Title: String,
    val Description: String,
    val CreationDate: String,
    val UpdatedDate: String,
    val Status: Int,
    val Images: List<Image>,
    val CategoryName: String
)

data class Image(
    val ImgId: Int = 0,      // Agregar ImgId por compatibilidad
    val PostId: Int = 0,     // Este campo lo llena el backend
    val Img: String          // Renombrar de Base64 a Img
)