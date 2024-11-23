import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.petcircle_proyectopsm.ImageAdapter
import com.example.petcircle_proyectopsm.Post
import com.example.petcircle_proyectopsm.databinding.ViewPostItemBinding


interface PostClickedListener {
    fun onPostClicked(post: Post)
} //una funcion para definir una accion que se hara cuando se haga click en un elemoento del recycler view

class PostAdapter(
    private var posts: List<Post>,
    private val listener: (Post) -> Unit // Lambda para manejar clics
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ViewPostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        // Asignamos los datos a las vistas
        holder.binding.PostTitle.text = post.Title
        holder.binding.PostBody.text = post.Description
        holder.binding.category.text = post.CategoryName
        holder.binding.data.text = post.CreationDate

        // Configurar ViewPager2 si hay imágenes
        if (post.Images.isNotEmpty()) {
            val images = post.Images.mapNotNull { it.Img.takeIf { img -> img.isNotEmpty() } }
            val imageAdapter = ImageAdapter(images)
            holder.binding.imageContainer.adapter = imageAdapter
        }

        // Configurar el clic en el elemento
        holder.binding.root.setOnClickListener {
            listener(post) // Llamamos al listener con el post actual
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

