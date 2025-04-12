package com.uniandes.vinilos.di

import com.uniandes.vinilos.repository.AlbumRepository
import com.uniandes.vinilos.viewmodel.AlbumDetailViewModel
import com.uniandes.vinilos.viewmodel.AlbumListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AlbumListViewModel(get()) }
    viewModel { AlbumDetailViewModel(get()) }
}

val repositoryModule = module {
    single { AlbumRepository(get()) }
} 