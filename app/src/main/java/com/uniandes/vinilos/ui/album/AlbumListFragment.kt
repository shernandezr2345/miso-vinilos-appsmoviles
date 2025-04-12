package com.uniandes.vinilos.ui.album

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
        view.findViewById<View>(R.id.addAlbumFab)?.also { fab ->
            if (userSession.isCollector()) {
                fab.visibility = View.VISIBLE
                fab.setOnClickListener {
                    findNavController().navigate(R.id.action_albums_to_addAlbum)
                }
            } else {
                fab.visibility = View.GONE
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
        holder.bind(getItem(position))
    }

    class AlbumViewHolder(
        view: View,
        private val onAlbumClick: (Album) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val coverImageView: ImageView = view.findViewById(R.id.albumCover)
        private val titleTextView: TextView = view.findViewById(R.id.albumTitle)
        private val genreTextView: TextView = view.findViewById(R.id.albumGenre)

        fun bind(album: Album) {
            titleTextView.text = album.name
            genreTextView.text = album.performers.firstOrNull()?.name ?: ""
            
            Glide.with(itemView.context)
                .load(album.cover)
                .placeholder(R.drawable.ic_album_placeholder)
                .error(R.drawable.ic_album_placeholder)
                .into(coverImageView)

            itemView.setOnClickListener { onAlbumClick(album) }
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