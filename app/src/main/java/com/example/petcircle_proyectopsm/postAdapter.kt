import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petcircle_proyectopsm.Post
import com.example.petcircle_proyectopsm.databinding.ViewPostItemBinding

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewPostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    class ViewHolder(private val binding: ViewPostItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.PostTitle.text = post.Title
            binding.PostBody.text = post.Description
            binding.category.text = post.CategoryId.toString() // Ajusta según lo esperado
            binding.data.text = post.CreationDate // Ajusta según lo esperado

            // Aquí se manejan las imágenes, cargándolas con Glide
            if (!post.Images.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(post.Images[0]) // Aquí asumes que la primera imagen se muestra
                    .into(binding.PostImg) // Asegúrate de que tienes un ImageView con este id en tu layout
            }
        }
    }
}
