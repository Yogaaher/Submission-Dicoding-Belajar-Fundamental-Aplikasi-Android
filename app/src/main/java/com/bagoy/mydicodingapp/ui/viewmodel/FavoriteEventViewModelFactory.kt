package com.bagoy.mydicodingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bagoy.mydicodingapp.data.repository.FavoriteEventRepository
import com.bagoy.mydicodingapp.ui.favorite.FavoriteViewModel

class FavoriteEventViewModelFactory(
    private val favoriteEventRepository: FavoriteEventRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                FavoriteViewModel(favoriteEventRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}