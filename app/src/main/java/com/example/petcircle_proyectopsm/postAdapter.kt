package com.example.petcircle_proyectopsm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.petcircle_proyectopsm.databinding.ViewPostItemBinding

class PostAdapter(private val posts : List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewPostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(posts[position])
    }


    class ViewHolder(private val binding: ViewPostItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(post: Post){
            binding.PostTitle.text = post.title
            binding.PostBody.text = post.body
            binding.category.text = post.category
            binding.data.text = post.date + "  " + post.hour
            //binding.PostImg.setImageResource(post.imageUrl) = post.imageUrl
        }
    }
}