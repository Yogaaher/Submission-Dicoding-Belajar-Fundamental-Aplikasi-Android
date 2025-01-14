package com.bagoy.mydicodingapp.ui.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagoy.mydicodingapp.data.database.FavoriteEvent
import com.bagoy.mydicodingapp.data.repository.FavoriteEventRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: FavoriteEventRepository) : ViewModel() {

    private val _favoriteList = MutableLiveData<List<FavoriteEvent>>()
    val favoriteList: LiveData<List<FavoriteEvent>> = _favoriteList

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadFavorites() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val favorites = repository.getAllFavoritesSync()
                _favoriteList.value = favorites
                Log.d("FavoriteViewModel", "Favorites loaded: ${favorites.size} items")
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun checkIsFavorite(eventId: Int) {
        viewModelScope.launch {
            try {
                val favoriteEvent = repository.isFavoriteSync(eventId)
                _isFavorite.value = favoriteEvent != null
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addFavorite(favoriteEvent: FavoriteEvent) {
        viewModelScope.launch {
            try {
                repository.addFavorite(favoriteEvent)
                _isFavorite.value = true
                loadFavorites()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun removeFavorite(eventId: Int) {
        viewModelScope.launch {
            try {
                repository.removeFavorite(eventId)
                _isFavorite.value = false
                Log.d("FavoriteViewModel", "Favorite event removed: $eventId")
                loadFavorites()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

}

