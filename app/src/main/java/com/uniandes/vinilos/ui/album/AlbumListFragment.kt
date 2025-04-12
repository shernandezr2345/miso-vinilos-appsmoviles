package com.uniandes.vinilos.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.vinilos.R
import com.uniandes.vinilos.model.Album
import com.uniandes.vinilos.viewmodel.AlbumListViewModel

class AlbumListFragment : Fragment() {
    private lateinit var viewModel: AlbumListViewModel
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

        viewModel = ViewModelProvider(this)[AlbumListViewModel::class.java]

        setupRecyclerView(view)
        observeViewModel()
        viewModel.loadAlbums()
    }

    private fun setupRecyclerView(view: View) {
        adapter = AlbumAdapter { album ->
            val bundle = Bundle().apply {
                putInt("albumId", album.id)
            }
            findNavController().navigate(R.id.action_albumListFragment_to_albumDetailFragment, bundle)
        }

        view.findViewById<RecyclerView>(R.id.albumsList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@AlbumListFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            adapter.submitList(albums)
        }
    }
}

class AlbumAdapter(
    private val onAlbumClick: (Album) -> Unit
) : ListAdapter<Album, AlbumAdapter.AlbumViewHolder>(AlbumDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return AlbumViewHolder(view, onAlbumClick)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AlbumViewHolder(
        view: View,
        private val onAlbumClick: (Album) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val textView = view.findViewById<TextView>(android.R.id.text1)

        fun bind(album: Album) {
            textView.text = album.name
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