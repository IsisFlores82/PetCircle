package com.example.petcircle_proyectopsm.model

data class User(
    val UserId: Int,
    val FullName: String,
    val Password: String,
    val PhoneNumber: String,
    val NickName: String,
    val Img: String, // En base64
    val Email: String,
    val Status: Int,
    val CreationDate: String,
    val UpdatedDate: String
)