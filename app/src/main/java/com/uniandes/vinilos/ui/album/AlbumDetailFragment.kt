package com.uniandes.vinilos.ui.album

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.uniandes.vinilos.R
import com.uniandes.vinilos.databinding.FragmentAlbumDetailBinding
import com.uniandes.vinilos.models.Album
import com.uniandes.vinilos.models.Performer
import com.uniandes.vinilos.models.Track
import com.uniandes.vinilos.viewmodel.AlbumDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

@AndroidEntryPoint
class AlbumDetailFragment : Fragment() {
    private val viewModel: AlbumDetailViewModel by viewModels()
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var performersAdapter: PerformerAdapter

    // Usar view binding para evitar findViewById repetitivos
    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!

    // Constantes para formateo de fecha - evita crear objetos repetidamente
    companion object {
        // Definir formatos de fecha como constantes para reutilización
        private val DATE_PARSER = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val DATE_FORMATTER = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        private const val UNKNOWN_ARTIST = "Unknown Artist"
    }

    // Crear opciones reutilizables para Glide
    private val glideOptions by lazy {
        RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_album_placeholder)
            .error(R.drawable.ic_album_placeholder)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupObservers()

        // Usar también takeIf para prevenir operaciones innecesarias
        arguments?.getInt("albumId", -1).takeIf { it != -1 }?.let { albumId ->
            viewModel.loadAlbum(albumId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpieza de recursos para evitar memory leaks
        _binding = null
    }

    private fun setupRecyclerViews() {
        // Inicializar adapters de manera eficiente
        tracksAdapter = TrackAdapter()
        performersAdapter = PerformerAdapter()

        // Uso de setHasFixedSize para mejor rendimiento cuando el tamaño del layout no cambia
        binding.tracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tracksAdapter
            setHasFixedSize(true)
        }

        binding.performersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = performersAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        // Usar lifecycleScope para operaciones sensibles al ciclo de vida
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.album.observe(viewLifecycleOwner) { album ->
                updateAlbumUI(album)
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

    // Extraer la lógica de actualización de UI a un método separado para mejor legibilidad
    private fun updateAlbumUI(album: Album) {
        with(binding) {
            albumTitleTextView.text = album.name

            // Usar el operador Elvis para una expresión condicional más concisa
            albumArtistTextView.text = album.performers.firstOrNull()?.name ?: UNKNOWN_ARTIST

            // Formatear fecha de manera segura y eficiente
            albumGenreTextView.text = album.genre
            albumReleaseDateTextView.text = formatReleaseDate(album.releaseDate)
            albumDescriptionTextView.text = album.description

            // Cargar imagen con opciones optimizadas
            Glide.with(requireContext())
                .load(album.cover)
                .apply(glideOptions)
                .into(albumCoverImageView)

            // Actualizar adapters de manera eficiente
            tracksAdapter.submitList(album.tracks)
            tracksRecyclerView.isVisible = album.tracks.isNotEmpty()
            performersAdapter.submitList(album.performers)
        }
    }

    // Extraer el formateo de fecha a un método separado para reutilización
    private fun formatReleaseDate(dateString: String): String {
        return try {
            val date = DATE_PARSER.parse(dateString)
            // Solo formatea si la fecha se pudo parsear correctamente
            if (date != null) DATE_FORMATTER.format(date) else dateString
        } catch (e: Exception) {
            dateString
        }
    }
}

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    private var tracks: List<Track> = emptyList()

    // Cache para duración formateada - evita recalcular en cada bind
    private val durationCache = ConcurrentHashMap<String, String>()

    fun submitList(newTracks: List<Track>) {
        val oldList = tracks
        tracks = newTracks

        // Solo notificar cambios si la lista realmente cambió
        if (oldList != newTracks) {
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view, durationCache)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    class TrackViewHolder(
        itemView: View,
        private val durationCache: ConcurrentHashMap<String, String>
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.trackNameTextView)
        private val durationTextView: TextView = itemView.findViewById(R.id.trackDurationTextView)

        fun bind(track: Track) {
            nameTextView.text = track.name

            // Usar cache para evitar recalcular la duración formateada
            durationTextView.text = durationCache.getOrPut(track.duration) {
                formatDuration(track.duration)
            }
        }

        // Extraer el formateo de duración a un método para reutilización
        private fun formatDuration(durationStr: String): String {
            val durationInSeconds = durationStr.toIntOrNull() ?: 0
            val minutes = durationInSeconds / 60
            val seconds = durationInSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }
    }
}

class PerformerAdapter : RecyclerView.Adapter<PerformerAdapter.PerformerViewHolder>() {
    private var performers: List<Performer> = emptyList()

    // RequestOptions reutilizables para Glide
    private val glideOptions by lazy {
        RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_performer_placeholder)
            .error(R.drawable.ic_performer_placeholder)
    }

    fun submitList(newPerformers: List<Performer>) {
        val oldList = performers
        performers = newPerformers

        // Solo notificar cambios si la lista realmente cambió
        if (oldList != newPerformers) {
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_performer, parent, false)
        return PerformerViewHolder(view, glideOptions)
    }

    override fun onBindViewHolder(holder: PerformerViewHolder, position: Int) {
        holder.bind(performers[position])
    }

    override fun getItemCount() = performers.size

    class PerformerViewHolder(
        itemView: View,
        private val glideOptions: RequestOptions
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.performerNameTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.performerImageView)

        fun bind(performer: Performer) {
            nameTextView.text = performer.name

            // Usar opciones preconfiguradas para Glide
            Glide.with(itemView.context)
                .load(performer.image)
                .apply(glideOptions)
                .into(imageView)
        }
    }
}