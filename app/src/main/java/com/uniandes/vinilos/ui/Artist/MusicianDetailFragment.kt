package com.uniandes.vinilos.ui.Artist

import android.os.Bundle
import android.util.Log
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
import com.uniandes.vinilos.models.PerformerPrizes
import com.uniandes.vinilos.models.Track
import com.uniandes.vinilos.ui.album.PerformerAdapter
import com.uniandes.vinilos.viewmodel.MusicianDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicianDetailFragment : Fragment() {
    private val viewModel: MusicianDetailViewModel by viewModels()
    private lateinit var albumsAdapter: AlbumAdapter
    private lateinit var performersPrizesAdapter: PerformerPrizesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_artist_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews(view)
        setupObservers(view)
        
        arguments?.getInt("musicianId")?.let { musicianId ->
            viewModel.loadMusician(musicianId)
        }
    }

    private fun setupRecyclerViews(view: View) {
        albumsAdapter = AlbumAdapter()
        performersPrizesAdapter = PerformerPrizesAdapter()


        view.findViewById<RecyclerView>(R.id.albumsRecyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = albumsAdapter
        }

        view.findViewById<RecyclerView>(R.id.performersPrizesRecyclerView).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = performersPrizesAdapter
        }
    }

    private fun setupObservers(view: View) {
        val loadingProgressBar = view.findViewById<View>(R.id.loadingProgressBar)
        val errorTextView = view.findViewById<TextView>(R.id.errorTextView)
        val contentView = view.findViewById<View>(R.id.contentLayout)
        val coverImageView = view.findViewById<ImageView>(R.id.albumCoverImageView)
        val titleTextView = view.findViewById<TextView>(R.id.artistNameTextView)
        val biographyTextView = view.findViewById<TextView>(R.id.biographyTextView)
        val bithDateTextView = view.findViewById<TextView>(R.id.birthDateTextView)

        // Verificación de que los views se encontraron
        if (titleTextView == null) {
            Log.e("setupObservers", "artistNameTextView es null")
            return
        }

        if (coverImageView == null) {
            Log.e("setupObservers", "albumCoverImageView es null")
            return
        }


        viewModel.musician.observe(viewLifecycleOwner) { musician ->
            musician?.let {
                titleTextView.text = musician.name
                biographyTextView.text = musician.description
                bithDateTextView.text = musician.birthDate.take(10)

                Glide.with(requireContext())
                    .load(it.image)
                    .placeholder(R.drawable.ic_album_placeholder)
                    .error(R.drawable.ic_album_placeholder)
                    .into(coverImageView)

                albumsAdapter.submitList(musician.albums)
                performersPrizesAdapter.submitList(musician.performerPrizes)
            }
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

class AlbumAdapter : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {
    private var albums: List<Album> = emptyList()

    fun submitList(newAlbums: List<Album>) {
        albums = newAlbums
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(albums[position])
    }

    override fun getItemCount() = albums.size

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.albumTitle)
        private val imageView: ImageView = itemView.findViewById(R.id.albumCover)


        fun bind(album: Album) {
            nameTextView.text = album.name

            Glide.with(itemView.context)
                .load(album.cover)
                .placeholder(R.drawable.ic_performer_placeholder)
                .error(R.drawable.ic_performer_placeholder)
                .into(imageView)

        }
    }
}

class PerformerPrizesAdapter : RecyclerView.Adapter<PerformerPrizesAdapter.PerformerPrizesViewHolder>() {
    private var performersPrizes: List<PerformerPrizes> = emptyList()

    fun submitList(newperformersPrizes: List<PerformerPrizes>) {
        performersPrizes = newperformersPrizes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformerPrizesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_performer_prize, parent, false)
        return PerformerPrizesViewHolder(view)
    }

    override fun onBindViewHolder(holder: PerformerPrizesViewHolder, position: Int) {
        holder.bind(performersPrizes[position])
    }

    override fun getItemCount() = performersPrizes.size

    class PerformerPrizesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val premiationDateTextView: TextView = itemView.findViewById(R.id.premiationDateTextView)

        fun bind(performerPrizes: PerformerPrizes) {
            premiationDateTextView.text = performerPrizes.premiationDate.take(10)

        }
    }
} 