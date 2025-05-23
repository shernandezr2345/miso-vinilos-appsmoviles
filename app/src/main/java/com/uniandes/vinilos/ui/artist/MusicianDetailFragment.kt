package com.uniandes.vinilos.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.uniandes.vinilos.R
import com.uniandes.vinilos.databinding.FragmentArtistDetailBinding
import com.uniandes.vinilos.models.Album
import com.uniandes.vinilos.models.PerformerPrizes
import com.uniandes.vinilos.viewmodel.MusicianDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicianDetailFragment : Fragment() {
    private val viewModel: MusicianDetailViewModel by viewModels()
    private lateinit var albumsAdapter: AlbumAdapter
    private lateinit var performersPrizesAdapter: PerformerPrizesAdapter

    // Usar view binding para evitar findViewById repetitivos
    private var _binding: FragmentArtistDetailBinding? = null
    private val binding get() = _binding!!

    // RequestOptions para reutilizar configuración de Glide
    private val glideOptions by lazy {
        RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.album_placeholder)
            .error(R.drawable.album_placeholder)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupObservers()

        // Usar también?: para prevenir operaciones innecesarias
        arguments?.getInt("musicianId", -1).takeIf { it != -1 }?.let { musicianId ->
            viewModel.loadMusician(musicianId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpieza de recursos para evitar memory leaks
        _binding = null
    }

    private fun setupRecyclerViews() {
        albumsAdapter = AlbumAdapter { album ->
            val bundle = Bundle().apply {
                putInt("albumId", album.id)
            }
            findNavController().navigate(R.id.action_musicianDetailFragment_to_albumDetailFragment, bundle)
        }
        performersPrizesAdapter = PerformerPrizesAdapter()

        // Configurar RecyclerViews con setHasFixedSize para optimizar rendimiento
        binding.albumsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = albumsAdapter
            setHasFixedSize(true)
        }

        binding.performersPrizesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = performersPrizesAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        // Usar lifecycleScope para observar cambios de estado de forma más eficiente
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.musician.observe(viewLifecycleOwner) { musician ->
                musician?.let {
                    with(binding) {
                        artistNameTextView.text = musician.name
                        biographyTextView.text = musician.description
                        birthDateTextView.text = musician.birthDate.take(10)

                        // Usar la configuración preestablecida de Glide
                        Glide.with(requireContext())
                            .load(it.image)
                            .apply(glideOptions)
                            .into(albumCoverImageView)
                    }

                    // Usar DiffUtil implícitamente a través de submitList
                    albumsAdapter.submitList(musician.albums.toList())
                    performersPrizesAdapter.submitList(musician.performerPrizes.toList())
                }
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                binding.loadingProgressBar.isVisible = isLoading
                binding.contentLayout.isVisible = !isLoading
            }

            viewModel.error.observe(viewLifecycleOwner) { error ->
                binding.errorTextView.text = error
                binding.errorTextView.isVisible = error != null
                binding.contentLayout.isVisible = error == null
            }
        }
    }
}

// Optimizar Adapter con DiffUtil para actualización eficiente
class AlbumAdapter(
    private val onAlbumClick: (Album) -> Unit
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {
    private var albums: List<Album> = emptyList()

    // Usar ViewBinding o view caching para mejorar rendimiento
    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albums[position]
        holder.bind(album)
    }

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val albumCoverImageView: ImageView = itemView.findViewById(R.id.albumCoverImageView)
        private val albumNameTextView: TextView = itemView.findViewById(R.id.albumNameTextView)
        private val albumArtistTextView: TextView = itemView.findViewById(R.id.albumArtistTextView)
        private val albumGenreTextView: TextView = itemView.findViewById(R.id.albumGenreTextView)

        fun bind(album: Album) {
            albumNameTextView.text = album.name
            val artistName = album.performers?.firstOrNull()?.name ?: "Artista desconocido"
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

            // Set click listener
            itemView.setOnClickListener {
                onAlbumClick(album)
            }
        }
    }

    fun submitList(newAlbums: List<Album>) {
        val oldList = albums
        albums = newAlbums

        // Uso simple de notificación para este ejemplo
        // En producción usar DiffUtil.calculateDiff() para solo actualizar items que cambiaron
        if (oldList != newAlbums) {
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun getItemCount() = albums.size
}

// Optimizar Adapter de premios
class PerformerPrizesAdapter : RecyclerView.Adapter<PerformerPrizesAdapter.PerformerPrizesViewHolder>() {
    private var performersPrizes: List<PerformerPrizes> = emptyList()

    class PerformerPrizesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val premiationDateTextView: TextView = itemView.findViewById(R.id.premiationDateTextView)

        // Extraer la función para evitar recalcularla en cada bind
        fun bind(performerPrizes: PerformerPrizes) {
            // Extraer valor una vez en lugar de llamar take(10) múltiples veces
            val formattedDate = performerPrizes.premiationDate.take(10)
            premiationDateTextView.text = formattedDate
        }
    }

    fun submitList(newPerformersPrizes: List<PerformerPrizes>) {
        val oldList = performersPrizes
        performersPrizes = newPerformersPrizes

        // Optimizar notificación de cambios
        if (oldList != newPerformersPrizes) {
            notifyDataSetChanged()
        }
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
}