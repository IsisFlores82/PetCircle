package com.example.petcircle_proyectopsm

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.util.Base64

class ImageAdapter(private val images: List<String>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = ImageView(parent.context)
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        // Decodificar imagen base64 y cargarla
        val decodedString: ByteArray = Base64.decode(image.split(",")[1], Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        holder.imageView.setImageBitmap(decodedByte)
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
