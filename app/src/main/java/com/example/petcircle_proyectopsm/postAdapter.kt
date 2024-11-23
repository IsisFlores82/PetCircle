import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.petcircle_proyectopsm.ImageAdapter
import com.example.petcircle_proyectopsm.Post
import com.example.petcircle_proyectopsm.databinding.ViewPostItemBinding

class PostAdapter(private var posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ViewPostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.binding.PostTitle.text = post.Title
        holder.binding.PostBody.text = post.Description
        holder.binding.category.text = post.CategoryName
        holder.binding.data.text = post.CreationDate

        // Configuramos el ViewPager2 si hay imágenes en el post
        if (post.Images.isNotEmpty()) {
            val images = post.Images.mapNotNull { it.Img.takeIf { img -> img.isNotEmpty() } }
            val imageAdapter = ImageAdapter(images)
            holder.binding.imageContainer.adapter = imageAdapter
        }
    }

    override fun getItemCount(): Int = posts.size

    // Método para actualizar la lista de posts
    fun updatePosts(newPosts: List<Post>) {
        this.posts = newPosts
        notifyDataSetChanged()  // Actualiza el RecyclerView
    }

    fun filtrar(listaFiltrada: List<Post>) {
        this.posts = listaFiltrada
        notifyDataSetChanged()

    }

    inner class PostViewHolder(val binding: ViewPostItemBinding) : RecyclerView.ViewHolder(binding.root)
}

