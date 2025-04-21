package com.uniandes.vinilos.ui.Artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uniandes.vinilos.R
import com.uniandes.vinilos.models.Album
import com.uniandes.vinilos.models.Performer
import com.uniandes.vinilos.models.Track
import com.uniandes.vinilos.viewmodel.AlbumDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicianDetailFragment : Fragment() {
    private val viewModel: AlbumDetailViewModel by viewModels()
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var performersAdapter: PerformerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_album_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews(view)
        setupObservers(view)
        
        arguments?.getInt("albumId")?.let { albumId ->
            viewModel.loadAlbum(albumId)
        }
    }

    private fun setupRecyclerViews(view: View) {
        tracksAdapter = TrackAdapter()
        performersAdapter = PerformerAdapter()

        view.findViewById<RecyclerView>(R.id.tracksRecyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tracksAdapter
        }

        view.findViewById<RecyclerView>(R.id.performersRecyclerView).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = performersAdapter
        }
    }

    private fun setupObservers(view: View) {
        val loadingProgressBar = view.findViewById<View>(R.id.loadingProgressBar)
        val errorTextView = view.findViewById<TextView>(R.id.errorTextView)
        val contentView = view.findViewById<View>(R.id.contentLayout)
        val coverImageView = view.findViewById<ImageView>(R.id.albumCoverImageView)
        val titleTextView = view.findViewById<TextView>(R.id.albumTitleTextView)
        val artistTextView = view.findViewById<TextView>(R.id.albumArtistTextView)
        val genreTextView = view.findViewById<TextView>(R.id.albumGenreTextView)
        val releaseDateTextView = view.findViewById<TextView>(R.id.albumReleaseDateTextView)
        val descriptionTextView = view.findViewById<TextView>(R.id.albumDescriptionTextView)

        viewModel.album.observe(viewLifecycleOwner) { album ->
            titleTextView.text = album.name
            artistTextView.text = album.performers.firstOrNull()?.name ?: ""
            genreTextView.text = album.genre
            releaseDateTextView.text = album.releaseDate
            descriptionTextView.text = album.description

            Glide.with(requireContext())
                .load(album.cover)
                .placeholder(R.drawable.ic_album_placeholder)
                .error(R.drawable.ic_album_placeholder)
                .into(coverImageView)

            tracksAdapter.submitList(album.tracks)
            performersAdapter.submitList(album.performers)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loadingProgressBar.isVisible = isLoading
            contentView.isVisible = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            errorTextView.text = error
            errorTextView.isVisible = error != null
            contentView.isVisible = error == null
        }
    }
}

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    private var tracks: List<Track> = emptyList()

    fun submitList(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
        private val durationTextView: TextView = itemView.findViewById(R.id.trackDurationTextView)

        fun bind(track: Track) {
            nameTextView.text = track.name
            durationTextView.text = track.duration
        }
    }
}

class PerformerAdapter : RecyclerView.Adapter<PerformerAdapter.PerformerViewHolder>() {
    private var performers: List<Performer> = emptyList()

    fun submitList(newPerformers: List<Performer>) {
        performers = newPerformers
        notifyDataSetChanged()
    }

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