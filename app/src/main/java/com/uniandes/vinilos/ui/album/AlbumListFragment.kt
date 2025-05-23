package com.uniandes.vinilos.ui.album

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uniandes.vinilos.R
import com.uniandes.vinilos.models.Album
import com.uniandes.vinilos.ui.base.BaseFragment
import com.uniandes.vinilos.viewmodel.AlbumListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumListFragment : BaseFragment() {
    private val viewModel: AlbumListViewModel by viewModels()
    private lateinit var adapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_album_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        setupSearchView(view)
        setupFab(view)
        observeViewModel(view)
        viewModel.loadAlbums()
    }

    private fun setupRecyclerView(view: View) {
        adapter = AlbumAdapter { album ->
            val bundle = Bundle().apply {
                putInt("albumId", album.id)
            }
            findNavController().navigate(R.id.action_albumListFragment_to_albumDetailFragment, bundle)
        }

        view.findViewById<RecyclerView>(R.id.albumsRecyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@AlbumListFragment.adapter
        }
    }

    private fun setupSearchView(view: View) {
        view.findViewById<EditText>(R.id.searchEditText).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchAlbums(s?.toString() ?: "")
            }
        })
    }

    private fun setupFab(view: View) {
        view.findViewById<View>(R.id.addAlbumButton)?.also { button ->
            if (userSession.isCollector()) {
                button.visibility = View.VISIBLE
                button.setOnClickListener {
                    findNavController().navigate(R.id.action_albumListFragment_to_addAlbumFragment)
                }
            } else {
                button.visibility = View.GONE
            }
        }
    }

    private fun observeViewModel(view: View) {
        val loadingProgressBar = view.findViewById<View>(R.id.loadingProgressBar)
        val errorTextView = view.findViewById<TextView>(R.id.errorTextView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.albumsRecyclerView)

        viewModel.filteredAlbums.observe(viewLifecycleOwner) { albums ->
            adapter.submitList(albums)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loadingProgressBar.isVisible = isLoading
            recyclerView.isVisible = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            errorTextView.text = error
            errorTextView.isVisible = error != null
            recyclerView.isVisible = error == null
        }
    }
}

class AlbumAdapter(
    private val onAlbumClick: (Album) -> Unit
) : ListAdapter<Album, AlbumAdapter.AlbumViewHolder>(AlbumDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view, onAlbumClick)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = getItem(position)
        holder.bind(album)
    }

    inner class AlbumViewHolder(
        itemView: View,
        private val onAlbumClick: (Album) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val albumCoverImageView: ImageView = itemView.findViewById(R.id.albumCoverImageView)
        private val albumNameTextView: TextView = itemView.findViewById(R.id.albumNameTextView)
        private val albumArtistTextView: TextView = itemView.findViewById(R.id.albumArtistTextView)
        private val albumGenreTextView: TextView = itemView.findViewById(R.id.albumGenreTextView)
        private val editOptionsGroup: LinearLayout = itemView.findViewById(R.id.editOptionsGroup)
        private val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(album: Album) {
            albumNameTextView.text = album.name
            val artistName = album.performers.firstOrNull()?.name ?: "Artista desconocido"
            albumArtistTextView.text = artistName
            albumGenreTextView.text = album.genre
            
            // Load album cover image
            Glide.with(albumCoverImageView.context)
                .load(album.cover)
                .placeholder(R.drawable.album_placeholder)
                .error(R.drawable.album_placeholder)
                .into(albumCoverImageView)

            // Set content descriptions for accessibility
            albumCoverImageView.contentDescription = "Portada del álbum ${album.name}"
            albumNameTextView.contentDescription = "Nombre del álbum: ${album.name}"
            albumArtistTextView.contentDescription = "Artista: $artistName"
            albumGenreTextView.contentDescription = "Género: ${album.genre}"
            editButton.contentDescription = "Editar álbum ${album.name}"
            deleteButton.contentDescription = "Eliminar álbum ${album.name}"

            // Show edit options only for collectors
            editOptionsGroup.visibility = View.GONE // We'll handle this in the fragment

            // Set click listeners
            itemView.setOnClickListener {
                onAlbumClick(album)
            }

            editButton.setOnClickListener {
                // Implement edit logic
            }

            deleteButton.setOnClickListener {
                // Implement delete logic
            }
        }
    }
}

class AlbumDiffCallback : DiffUtil.ItemCallback<Album>() {
    override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem == newItem
    }
} 