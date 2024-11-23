package com.example.petcircle_proyectopsm.repository

import android.util.Log
import com.example.petcircle_proyectopsm.Post
import com.example.petcircle_proyectopsm.db.DbHelper
import com.example.petcircle_proyectopsm.CreatePost

class PostRepository(private val dbHelper: DbHelper) {

    val createPost = CreatePost()

    fun synchronizePosts(){
        val posts = dbHelper.getUnsyncPosts()
        /*
        if(posts.size > 0){
            Log.e("TAG", "Se actualizarán los Posts")
            for (post in posts){
                var actualPostId = post.PostId
                post.PostId = 0
                try{
                    createPost.savePost(post)
                }catch (e: Exception){
                    Log.e("Exception", e.toString())
                } finally {

                }

            }
        }*/
    }

    fun saveUnsyncPost(post: Post){
        val PostId = dbHelper.saveUnsyncPost(post)

        if(PostId != -1L){
            for (img in post.Images){
                dbHelper.saveUnsyncImage(img)
            }
        }else{
            Log.e("TAG", "Error al guardar el Post en la base de datos local.")
        }

    }
}