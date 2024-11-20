package com.example.petcircle_proyectopsm.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserDbClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://petcircleapi.mypressonline.com")  //esta es la url de la api general, los endpoints se especifican en los metodos de la api
        .addConverterFactory(GsonConverterFactory.create()) //aqui se usa gsonConverter para convertir la respuesta de la api(json) en objetos kotlin
        .build()

    val service = retrofit.create(ThePetCircleDbService::class.java)    //atravez de esto se crea una interfaz donde estaran las solicitudes https, con esto se invocan los metodos get, post, etc
}