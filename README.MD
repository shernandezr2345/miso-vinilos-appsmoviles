# Aplicación Vinilos

Una aplicación móvil para gestionar y explorar discos de vinilo, construida con prácticas modernas de desarrollo Android.

## Arquitectura

La aplicación sigue el patrón de arquitectura MVVM (Model-View-ViewModel) con principios de Clean Architecture, proporcionando una clara separación de responsabilidades y haciendo el código más mantenible y testeable.

### Estructura y Componentes del Proyecto

#### Capa de Datos
1. **Modelos**
   - Ubicados en `app/src/main/java/com/uniandes/vinilos/models/`
   - Definen las estructuras de datos utilizadas en la aplicación
   - Ejemplo:
   ```kotlin
   data class Album(
       val id: Int,
       val name: String,
       val cover: String,
       val releaseDate: String,
       val description: String,
       val genre: String,
       val recordLabel: String,
       val tracks: List<Track>,
       val comments: List<Comment>,
       val performers: List<Performer>
   )
   ```

2. **DAOs (Objetos de Acceso a Datos)**
   - Ubicados en `app/src/main/java/com/uniandes/vinilos/data/dao/`
   - Definen interfaces para operaciones de datos
   - Implementados por DAOs de API que utilizan Retrofit
   - Ejemplo:
   ```kotlin
   interface AlbumDao {
       suspend fun getAllAlbums(): List<Album>
       suspend fun getAlbumById(id: Int): Album?
       suspend fun getAlbumTracks(albumId: Int): List<Track>
   }

   @ActivityRetainedScoped
   class ApiAlbumDao @Inject constructor(
       private val albumService: AlbumService
   ) : AlbumDao {
       override suspend fun getAllAlbums(): List<Album> {
           return try {
               val response = albumService.getAlbums()
               if (response.isSuccessful) {
                   response.body() ?: emptyList()
               } else {
                   emptyList()
               }
           } catch (e: Exception) {
               emptyList()
           }
       }
   }
   ```

3. **Servicios de Red**
   - Ubicados en `app/src/main/java/com/uniandes/vinilos/network/`
   - Definen endpoints de API usando Retrofit
   - Ejemplo:
   ```kotlin
   interface AlbumService {
       @GET("albums")
       suspend fun getAlbums(): Response<List<Album>>

       @GET("albums/{id}")
       suspend fun getAlbum(@Path("id") id: Int): Response<Album>

       @GET("albums/{id}/tracks")
       suspend fun getAlbumTracks(@Path("id") id: Int): Response<List<Track>>
   }
   ```

4. **Repositorios**
   - Ubicados en `app/src/main/java/com/uniandes/vinilos/data/repository/`
   - Actúan como fuente única de verdad para los datos
   - Utilizan DAOs para obtener datos de la red
   - Ejemplo:
   ```kotlin
   @Singleton
   class AlbumRepository @Inject constructor(
       private val albumDao: AlbumDao
   ) {
       suspend fun getAllAlbums(): List<Album> {
           return albumDao.getAllAlbums()
       }

       suspend fun getAlbumById(id: Int): Album? {
           return albumDao.getAlbumById(id)
       }

       suspend fun getAlbumTracks(albumId: Int): List<Track> {
           return albumDao.getAlbumTracks(albumId)
       }
   }
   ```

#### Inyección de Dependencias
- Utiliza Hilt para la inyección de dependencias
- Configuración de red en `NetworkModule.kt`:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://146.190.65.12:3000/"

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAlbumService(retrofit: Retrofit): AlbumService {
        return retrofit.create(AlbumService::class.java)
    }
}
```

#### Capa de UI
1. **Fragments**
   - Ubicados en `app/src/main/java/com/uniandes/vinilos/ui/fragments/`
   - Manejan el renderizado de UI y las interacciones del usuario
   - Ejemplo:
   ```kotlin
   class AlbumListFragment : Fragment() {
       private val viewModel: AlbumViewModel by viewModels()
       private lateinit var binding: FragmentAlbumListBinding
       
       override fun onCreateView(
           inflater: LayoutInflater,
           container: ViewGroup?,
           savedInstanceState: Bundle?
       ): View {
           binding = FragmentAlbumListBinding.inflate(inflater, container, false)
           return binding.root
       }
       
       override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
           super.onViewCreated(view, savedInstanceState)
           setupRecyclerView()
           observeViewModel()
       }
   }
   ```

2. **ViewModels**
   - Ubicados en `app/src/main/java/com/uniandes/vinilos/ui/viewmodel/`
   - Manejan la lógica de negocio y la transformación de datos
   - Ejemplo:
   ```kotlin
   class AlbumViewModel @Inject constructor(
       private val albumRepository: AlbumRepository
   ) : ViewModel() {
       private val _albums = MutableStateFlow<List<Album>>(emptyList())
       val albums: StateFlow<List<Album>> = _albums.asStateFlow()
       
       fun loadAlbums() {
           viewModelScope.launch {
               albumRepository.getAlbums().collect { albums ->
                   _albums.value = albums
               }
           }
       }
   }
   ```

### Implementación de Navegación

La aplicación utiliza el Navigation Component con una arquitectura de actividad única:

1. **Grafo de Navegación**
   - Ubicado en `app/src/main/res/navigation/nav_graph.xml`
   - Define todos los posibles caminos de navegación
   ```xml
   <navigation xmlns:android="http://schemas.android.com/apk/res/android"
       xmlns:app="http://schemas.android.com/apk/res-auto"
       android:id="@+id/nav_graph"
       app:startDestination="@id/roleSelectionFragment">
       
       <fragment
           android:id="@+id/roleSelectionFragment"
           android:name="com.uniandes.vinilos.ui.fragments.RoleSelectionFragment"
           android:label="Seleccionar Rol">
           <action
               android:id="@+id/action_roleSelection_to_albumList"
               app:destination="@id/albumListFragment" />
       </fragment>
       
       <fragment
           android:id="@+id/albumListFragment"
           android:name="com.uniandes.vinilos.ui.fragments.AlbumListFragment"
           android:label="Álbumes">
           <action
               android:id="@+id/action_albumList_to_albumDetail"
               app:destination="@id/albumDetailFragment" />
       </fragment>
   </navigation>
   ```

2. **Navegación en Fragments**
   ```kotlin
   // Navegar al detalle del álbum
   findNavController().navigate(
       AlbumListFragmentDirections.actionAlbumListToAlbumDetail(albumId)
   )
   
   // Navegar hacia atrás
   findNavController().navigateUp()
   ```

3. **Safe Args**
   - Utilizado para navegación con tipos seguros
   - Generado por el Navigation Component
   - Ejemplo:
   ```kotlin
   // En AlbumListFragment
   val action = AlbumListFragmentDirections
       .actionAlbumListToAlbumDetail(albumId)
   findNavController().navigate(action)
   
   // En AlbumDetailFragment
   val albumId = arguments?.let { 
       AlbumDetailFragmentArgs.fromBundle(it).albumId 
   }
   ```

## Pruebas

La aplicación utiliza pruebas instrumentadas con Espresso para verificar la funcionalidad de la UI.

### Estructura de Pruebas

Las pruebas se encuentran en:
- `app/src/androidTest/java/com/uniandes/vinilos/ui/albums/`
- Utilizan Espresso para pruebas de UI
- Se enfocan en la interacción con la interfaz de usuario

### Ejemplo de Prueba con Espresso

```kotlin
@HiltAndroidTest
class AlbumListFragmentTest {
    @Test
    fun testAlbumListFragment_UIElements() {
        // Configuración
        onView(withId(R.id.visitorRole)).perform(click())
        onView(withId(R.id.continueButton)).perform(click())
        
        // Verificación
        onView(withId(R.id.albumsRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAlbumDetailFragment_BasicInfo() {
        // Verificar que todos los elementos básicos de información se muestran
        onView(withId(R.id.albumCoverImageView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.albumTitleTextView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.albumArtistTextView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.albumGenreTextView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.albumReleaseDateTextView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.albumDescriptionTextView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAlbumDetailFragment_TracksList() {
        // Verificar que el RecyclerView de tracks se muestra
        onView(withId(R.id.tracksRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun testAlbumDetailFragment_PerformersList() {
        // Verificar que el RecyclerView de performers se muestra
        onView(withId(R.id.performersRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumChildCount(1)))
    }
}
```

### Ejecución de Pruebas

Hay dos formas de ejecutar las pruebas:

1. **A través de la línea de comandos:**
   ```bash
   ./gradlew connectedAndroidTest
   ```

2. **A través de Android Studio:**
   - Abrir el archivo de prueba
   - Hacer clic derecho sobre el archivo o método de prueba
   - Seleccionar "Run 'NombreDelTest'"
   - También se pueden ejecutar pruebas individuales haciendo clic en el ícono de "play" junto al método de prueba

Las pruebas verifican:
1. Visualización correcta de elementos UI
2. Interacción con botones y controles
3. Navegación entre pantallas
4. Carga y visualización de datos
5. Comportamiento de listas y RecyclerViews

## Construcción de la Aplicación

### Generación de APK

1. **APK de Depuración**
   ```bash
   ./gradlew assembleDebug
   ```
   Salida: `app/build/outputs/apk/debug/app-debug.apk`

2. **APK de Lanzamiento**
   ```bash
   ./gradlew assembleRelease
   ```
   Salida: `app/build/outputs/apk/release/app-release.apk`


## Dependencias

La aplicación utiliza varias bibliotecas modernas de Android:

- **Hilt**: Inyección de dependencias
- **Retrofit**: Llamadas de red
- **Coroutines**: Programación asíncrona
- **Flow**: Flujos reactivos
- **Navigation Component**: Manejo de navegación
- **ViewBinding**: Vinculación de vistas
- **Material Design**: Componentes de UI

## Estructura del Proyecto

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/uniandes/vinilos/
│   │   │   ├── models/          # Modelos de datos
│   │   │   ├── network/         # Servicios de API
│   │   │   ├── repository/      # Repositorios de datos
│   │   │   ├── ui/             # Componentes de UI
│   │   │   │   ├── fragments/   # Fragments
│   │   │   │   └── viewmodel/   # ViewModels
│   │   │   └── utils/          # Clases de utilidad
│   │   └── res/
│   │       ├── layout/         # Archivos de layout
│   │       └── navigation/     # Grafos de navegación
│   ├── test/                   # Pruebas unitarias
│   └── androidTest/            # Pruebas instrumentadas
```

## Comenzando

1. Clonar el repositorio
2. Abrir el proyecto en Android Studio
3. Sincronizar el proyecto con los archivos Gradle
4. Ejecutar la aplicación en un emulador o dispositivo físico
