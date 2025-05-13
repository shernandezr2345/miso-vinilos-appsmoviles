package com.uniandes.vinilos.ui.Artist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uniandes.vinilos.R
import com.uniandes.vinilos.models.Musician
import com.uniandes.vinilos.ui.base.BaseFragment
import com.uniandes.vinilos.viewmodel.MusicianListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicianListFragment : BaseFragment() {
    private val viewModel:  MusicianListViewModel by viewModels()
    private lateinit var adapter: MusicianAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_artist_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        setupSearchView(view)
        setupFab(view)
        observeViewModel(view)
        viewModel.loadMusicians()
    }

    private fun setupRecyclerView(view: View) {
        adapter = MusicianAdapter { musician ->
            val bundle = Bundle().apply {
                putInt("musicianId", musician.id)
            }
            findNavController().navigate(R.id.action_navigation_artists_to_musicianDetailFragment, bundle)
        }

        view.findViewById<RecyclerView>(R.id.artistsRecyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MusicianListFragment.adapter
        }
    }

    private fun setupSearchView(view: View) {
        view.findViewById<EditText>(R.id.searchEditText).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchMusicians(s?.toString() ?: "")
            }
        })
    }

    private fun setupFab(view: View) {
        view.findViewById<View>(R.id.addArtistFab)?.also { fab ->
            if (userSession.isCollector()) {
                fab.visibility = View.VISIBLE

            } else {
                fab.visibility = View.GONE
            }
        }
    }

    private fun observeViewModel(view: View) {
        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loadingProgressBar)
        val errorTextView = view.findViewById<TextView>(R.id.musicianErrorTextView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.artistsRecyclerView)

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

class MusicianAdapter(
    private val onMusicianClick: (Musician) -> Unit
) : ListAdapter<Musician, MusicianAdapter.MusicianViewHolder>(MusicianDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicianViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artist, parent, false)
        return MusicianViewHolder(view, onMusicianClick)
    }

    override fun onBindViewHolder(holder: MusicianViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MusicianViewHolder(
        view: View,
        private val onMusicianClick: (Musician) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val musicianImage: ImageView = view.findViewById(R.id.musicianImage)
        private val musicianName: TextView = view.findViewById(R.id.musicianName)
        private val musicianDetail: TextView = view.findViewById(R.id.musicianDetail)

        fun bind(musician: Musician) {
            musicianName.text = musician.name
            musicianDetail.text = musician.name ?: "Unknown Artist"

            Glide.with(itemView.context)
                .load(musician.image)
                .placeholder(R.drawable.ic_album_placeholder)
                .error(R.drawable.ic_album_placeholder)
                .into(musicianImage)

            itemView.setOnClickListener { onMusicianClick(musician) }
        }
    }

}

class MusicianDiffCallback : DiffUtil.ItemCallback<Musician>() {
    override fun areItemsTheSame(oldItem: Musician, newItem: Musician): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Musician, newItem: Musician): Boolean {
        return oldItem == newItem
    }
} 