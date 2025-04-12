package com.uniandes.vinilos.di

import com.uniandes.vinilos.data.dao.ApiAuthDao
import com.uniandes.vinilos.data.dao.AuthDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAuthDao(apiAuthDao: ApiAuthDao): AuthDao {
        return apiAuthDao
    }
} 