package com.uniandes.vinilos.di

import com.uniandes.vinilos.data.dao.ApiAlbumDao
import com.uniandes.vinilos.data.dao.ApiMusicianDao
import com.uniandes.vinilos.data.dao.CollectorDao
import com.uniandes.vinilos.data.dao.MusicianDao
import com.uniandes.vinilos.network.AlbumService
import com.uniandes.vinilos.network.MusicianService
import com.uniandes.vinilos.repositories.CollectorRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://146.190.65.12:3000/"
// private const val BASE_URL = "http://146.190.65.12:3000/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

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

    @Provides
    @Singleton
    fun provideApiAlbumDao(albumService: AlbumService): ApiAlbumDao {
        return ApiAlbumDao(albumService)
    }

    @Provides
    @Singleton
    fun provideMusicianService(retrofit: Retrofit): MusicianService {
        return retrofit.create(MusicianService::class.java)
    }

    @Provides
    @Singleton
    fun provideMusicianDao(musicianService: MusicianService): MusicianDao {
        return ApiMusicianDao(musicianService)
    }

    @Provides
    @Singleton
    fun provideCollectorDao(retrofit: Retrofit): CollectorDao {
        return retrofit.create(CollectorDao::class.java)
    }

    @Provides
    @Singleton
    fun provideCollectorRepository(collectorDao: CollectorDao): CollectorRepository {
        return CollectorRepository(collectorDao)
    }

} 