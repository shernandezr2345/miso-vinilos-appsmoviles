package com.uniandes.vinilos.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uniandes.vinilos.R
import com.uniandes.vinilos.model.Album
import com.uniandes.vinilos.model.Performer
import com.uniandes.vinilos.model.Track
import com.uniandes.vinilos.viewmodel.AlbumDetailViewModel

class AlbumDetailFragment : Fragment() {
    private lateinit var viewModel: AlbumDetailViewModel
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var performersAdapter: PerformersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_album_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumId = arguments?.getInt("albumId") ?: return

        viewModel = ViewModelProvider(this)[AlbumDetailViewModel::class.java]

        setupRecyclerViews(view)
        observeViewModel()
        viewModel.loadAlbum(albumId)
    }

    private fun setupRecyclerViews(view: View) {
        tracksAdapter = TracksAdapter()
        performersAdapter = PerformersAdapter()

        view.findViewById<RecyclerView>(R.id.tracksList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tracksAdapter
        }

        view.findViewById<RecyclerView>(R.id.performersList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = performersAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.album.observe(viewLifecycleOwner) { album ->
            updateUI(album)
        }
    }

    private fun updateUI(album: Album) {
        view?.apply {
            findViewById<ImageView>(R.id.albumCover).let { imageView ->
                Glide.with(this)
                    .load(album.cover)
                    .into(imageView)
            }

            findViewById<TextView>(R.id.albumName).text = album.name
            findViewById<TextView>(R.id.albumReleaseDate).text = "Release Date: ${album.releaseDate}"
            findViewById<TextView>(R.id.albumGenre).text = "Genre: ${album.genre}"
            findViewById<TextView>(R.id.albumDescription).text = album.description

            tracksAdapter.submitList(album.tracks)
            performersAdapter.submitList(album.performers)
        }
    }
}

class TracksAdapter : RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {
    private var tracks = listOf<Track>()

    fun submitList(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView = view.findViewById<TextView>(android.R.id.text1)

        fun bind(track: Track) {
            textView.text = "${track.name} - ${track.duration}"
        }
    }
}

class PerformersAdapter : RecyclerView.Adapter<PerformersAdapter.PerformerViewHolder>() {
    private var performers = listOf<Performer>()

    fun submitList(newPerformers: List<Performer>) {
        performers = newPerformers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return PerformerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PerformerViewHolder, position: Int) {
        holder.bind(performers[position])
    }

    override fun getItemCount() = performers.size

    class PerformerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView = view.findViewById<TextView>(android.R.id.text1)

        fun bind(performer: Performer) {
            textView.text = performer.name
        }
    }
} 