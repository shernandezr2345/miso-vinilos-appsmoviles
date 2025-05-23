package com.uniandes.vinilos.ui.collectors

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uniandes.vinilos.R
import com.uniandes.vinilos.data.dao.ApiAlbumDao
import com.uniandes.vinilos.models.Collector
import com.uniandes.vinilos.models.CollectorAlbum
import com.uniandes.vinilos.models.Comment
import com.uniandes.vinilos.models.Performer
import com.uniandes.vinilos.utils.UserSession
import com.uniandes.vinilos.viewmodel.CollectorViewModel
import com.uniandes.vinilos.viewmodel.AlbumListViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.uniandes.vinilos.data.repository.AlbumRepository
import com.uniandes.vinilos.models.Album
import androidx.lifecycle.LifecycleOwner

@AndroidEntryPoint
class CollectorsFragment : Fragment() {
    private val viewModel: CollectorViewModel by viewModels()
    private val albumListViewModel: AlbumListViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private var albumsMap: Map<Int, List<CollectorAlbum>> = emptyMap()
    private var allAlbums: List<Album> = emptyList()
    private var collectorAdapter: CollectorAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collectors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.collectors_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        searchEditText = view.findViewById(R.id.searchEditText)
        setupSearchView()

        albumListViewModel.albums.observe(viewLifecycleOwner) { albums ->
            allAlbums = albums
            collectorAdapter?.updateAllAlbums(albums)
        }
        albumListViewModel.loadAlbums()

        viewModel.collectorAlbums.observe(viewLifecycleOwner) { map ->
            albumsMap = map
            collectorAdapter?.updateAlbumsMap(map)
        }

        viewModel.collectors.observe(viewLifecycleOwner) { collectors ->
            collectorAdapter = CollectorAdapter(
                collectors, albumsMap, viewModel, viewLifecycleOwner, allAlbums,
                onAlbumAdded = { viewModel.fetchCollectors() }
            ) { _ -> }
            recyclerView.adapter = collectorAdapter
        }

        viewModel.loading.observe(viewLifecycleOwner) { _ ->
            // Handle loading state
        }

        viewModel.error.observe(viewLifecycleOwner) { _ ->
            // Handle error state
        }

        viewModel.fetchCollectors()
    }

    private fun setupSearchView() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                (recyclerView.adapter as? CollectorAdapter)?.filter(s?.toString() ?: "")
            }
        })
    }
}

class CollectorAdapter(
    private var collectors: List<Collector>,
    private var albumsMap: Map<Int, List<CollectorAlbum>>,
    private val viewModel: CollectorViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private var allAlbums: List<Album>,
    private val onAlbumAdded: () -> Unit,
    private val onCollectorClick: (Collector) -> Unit
) : RecyclerView.Adapter<CollectorAdapter.CollectorViewHolder>() {

    private var filteredCollectors = collectors

    fun updateAllAlbums(newAlbums: List<Album>) {
        allAlbums = newAlbums
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collector, parent, false)
        return CollectorViewHolder(view, allAlbums, viewModel, lifecycleOwner, onAlbumAdded)
    }

    override fun onBindViewHolder(holder: CollectorViewHolder, position: Int) {
        val collector = filteredCollectors[position]
        val albums = albumsMap[collector.id] ?: emptyList()
        holder.bind(collector, albums)
    }

    override fun getItemCount() = filteredCollectors.size

    fun filter(query: String) {
        filteredCollectors = if (query.isEmpty()) {
            collectors
        } else {
            collectors.filter { collector ->
                collector.name.contains(query, ignoreCase = true) ||
                collector.email.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun updateAlbumsMap(newMap: Map<Int, List<CollectorAlbum>>) {
        albumsMap = newMap
        notifyDataSetChanged()
    }

    class CollectorViewHolder(
        itemView: View,
        private val allAlbums: List<Album>,
        private val viewModel: CollectorViewModel,
        private val lifecycleOwner: LifecycleOwner,
        private val onAlbumAdded: () -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.collector_name)
        private val emailTextView: TextView = itemView.findViewById(R.id.collector_email)
        private val phoneTextView: TextView = itemView.findViewById(R.id.collector_phone)
        private val commentsRecyclerView: RecyclerView = itemView.findViewById(R.id.comments_recycler_view)
        private val performersRecyclerView: RecyclerView = itemView.findViewById(R.id.performers_recycler_view)
        private val albumsRecyclerView: RecyclerView = itemView.findViewById(R.id.albums_recycler_view)
        private val addAlbumButton: ImageButton = itemView.findViewById(R.id.button_add_album)

        fun bind(collector: Collector, albums: List<CollectorAlbum>) {
            nameTextView.text = collector.name
            emailTextView.text = collector.email
            phoneTextView.text = collector.telephone

            // Setup comments recycler view
            commentsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            commentsRecyclerView.adapter = CommentAdapter(collector.comments)

            // Setup performers recycler view
            performersRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            performersRecyclerView.adapter = PerformerAdapter(collector.favoritePerformers)

            // Setup albums recycler view
            albumsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            albumsRecyclerView.adapter = CollectorAlbumAdapter(albums)

            // Show add album button only for collectors
            val userSession = UserSession(itemView.context.applicationContext)
            if (userSession.isCollector()) {
                addAlbumButton.visibility = View.VISIBLE
                addAlbumButton.setOnClickListener {
                    showAddAlbumDialog(collector.id)
                }
            } else {
                addAlbumButton.visibility = View.GONE
            }
        }

        private fun showAddAlbumDialog(collectorId: Int) {
            val context = itemView.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_album_to_collector, null)
            val spinner = dialogView.findViewById<Spinner>(R.id.albumSpinner)

            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            val albumNames = allAlbums.map { it.name }
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, albumNames)
            spinner.adapter = adapter

            dialogView.findViewById<View>(R.id.submitButton).setOnClickListener {
                val selectedPosition = spinner.selectedItemPosition
                if (selectedPosition != -1) {
                    val selectedAlbum = allAlbums[selectedPosition]
                    viewModel.addAlbumToCollector(collectorId, selectedAlbum.id)
                    viewModel.addAlbumResult.observe(lifecycleOwner) { success ->
                        if (success) {
                            Toast.makeText(context, "Album added!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            onAlbumAdded()
                        } else {
                            Toast.makeText(context, "Failed to add album", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    }
                }
            }
            dialog.show()
        }
    }
}

class CommentAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount() = comments.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.comment_description)
        private val ratingTextView: TextView = itemView.findViewById(R.id.comment_rating)

        fun bind(comment: Comment) {
            descriptionTextView.text = comment.description
            ratingTextView.text = "Rating: ${comment.rating}/5"
        }
    }
}

class PerformerAdapter(private val performers: List<Performer>) : RecyclerView.Adapter<PerformerAdapter.PerformerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_performer, parent, false)
        return PerformerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PerformerViewHolder, position: Int) {
        holder.bind(performers[position])
    }

    override fun getItemCount() = performers.size

    class PerformerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.performerNameTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.performerImageView)

        fun bind(performer: Performer) {
            nameTextView.text = performer.name
            Glide.with(itemView.context)
                .load(performer.image)
                .placeholder(R.drawable.ic_performer_placeholder)
                .error(R.drawable.ic_performer_placeholder)
                .into(imageView)
        }
    }
}

class CollectorAlbumAdapter(private val collectorAlbums: List<CollectorAlbum>) : RecyclerView.Adapter<CollectorAlbumAdapter.CollectorAlbumViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectorAlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collector_album, parent, false)
        return CollectorAlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectorAlbumViewHolder, position: Int) {
        holder.bind(collectorAlbums[position])
    }

    override fun getItemCount() = collectorAlbums.size

    class CollectorAlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.album_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.album_price)
        private val statusTextView: TextView = itemView.findViewById(R.id.album_status)
        private val genreTextView: TextView = itemView.findViewById(R.id.album_genre)
        private val recordLabelTextView: TextView = itemView.findViewById(R.id.album_record_label)
        private val releaseDateTextView: TextView = itemView.findViewById(R.id.album_release_date)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.album_description)
        private val coverImageView: ImageView = itemView.findViewById(R.id.album_cover)

        fun bind(collectorAlbum: CollectorAlbum) {
            // Handle null album case
            if (collectorAlbum.album == null) {
                nameTextView.text = "Unknown Album"
                priceTextView.text = "Price: $${collectorAlbum.price}"
                statusTextView.text = "Status: ${collectorAlbum.status}"
                genreTextView.text = ""
                recordLabelTextView.text = ""
                releaseDateTextView.text = ""
                descriptionTextView.text = ""
                coverImageView.setImageResource(R.drawable.ic_album_placeholder)
            } else {
                nameTextView.text = collectorAlbum.album.name
                priceTextView.text = "Price: $${collectorAlbum.price}"
                statusTextView.text = "Status: ${collectorAlbum.status}"
                genreTextView.text = "Genre: ${collectorAlbum.album.genre}"
                recordLabelTextView.text = "Record Label: ${collectorAlbum.album.recordLabel}"
                releaseDateTextView.text = "Released: ${collectorAlbum.album.releaseDate.substring(0, 10)}"
                descriptionTextView.text = collectorAlbum.album.description
                
                Glide.with(itemView.context)
                    .load(collectorAlbum.album.cover)
                    .placeholder(R.drawable.ic_album_placeholder)
                    .error(R.drawable.ic_album_placeholder)
                    .into(coverImageView)
            }
        }
    }
} 