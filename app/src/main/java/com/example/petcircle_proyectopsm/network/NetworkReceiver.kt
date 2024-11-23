package com.example.petcircle_proyectopsm.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
class NetworkReceiver(private val onNetworkRestored: () -> Unit) : BroadcastReceiver() { //Hereda BroadcastReciever para detectar cambios de conexion
    override fun onReceive(context: Context?, intent: Intent?) { //Cuando detecta evento de conectividad
        //servicio para checar estado de redd
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //obtiene la red activa
        val activeNetwork = connectivityManager.activeNetwork

        //confirma que no sea nula
        if (activeNetwork != null) {
            //obtiene las capacidades de la red
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            //confirma que tenga capacidad de conectarse a internet
            val hasInternet = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

            if (hasInternet) {
                onNetworkRestored()
            }
        }
    }
}