package com.uniandes.vinilos.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uniandes.vinilos.R
import com.uniandes.vinilos.model.Performer
import com.uniandes.vinilos.model.Track
import com.uniandes.vinilos.viewmodel.AlbumDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlbumDetailFragment : Fragment() {
    private val viewModel: AlbumDetailViewModel by viewModel()
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
        setupRecyclerViews(view)
        setupObservers(view)
        viewModel.loadAlbum(albumId)
    }

    private fun setupRecyclerViews(view: View) {
        tracksAdapter = TracksAdapter()
        performersAdapter = PerformersAdapter()

        view.findViewById<RecyclerView>(R.id.tracksRecyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tracksAdapter
        }

        view.findViewById<RecyclerView>(R.id.performersRecyclerView).apply {
            layoutManager = LinearLayoutManager(context)
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

class TracksAdapter : RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {
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

    override fun getItemCount(): Int = tracks.size

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.trackNameTextView)
        private val durationTextView: TextView = view.findViewById(R.id.trackDurationTextView)

        fun bind(track: Track) {
            nameTextView.text = track.name
            durationTextView.text = track.duration
        }
    }
}

class PerformersAdapter : RecyclerView.Adapter<PerformersAdapter.PerformerViewHolder>() {
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

    override fun getItemCount(): Int = performers.size

    class PerformerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.performerNameTextView)
        private val imageView: ImageView = view.findViewById(R.id.performerImageView)

        fun bind(performer: Performer) {
            nameTextView.text = performer.name
            
            Glide.with(itemView.context)
                .load(performer.image)
                .placeholder(R.drawable.ic_album_placeholder)
                .error(R.drawable.ic_album_placeholder)
                .into(imageView)
        }
    }
} 