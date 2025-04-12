package com.uniandes.vinilos.di

import com.uniandes.vinilos.data.dao.AlbumDao
import com.uniandes.vinilos.data.dao.ApiAlbumDao
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DaoModule {
    
    @Binds
    @Singleton
    abstract fun bindAlbumDao(apiAlbumDao: ApiAlbumDao): AlbumDao
} 