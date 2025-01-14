package com.bagoy.mydicodingapp.data.repository

import android.app.Application
import com.bagoy.mydicodingapp.data.database.FavoriteEvent
import com.bagoy.mydicodingapp.data.database.FavoriteEventDao
import com.bagoy.mydicodingapp.data.database.FavoriteEventRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteEventRepository(application: Application) {
    private val favoriteEventDao: FavoriteEventDao

    init {
        val db = FavoriteEventRoomDatabase.getDatabase(application)
        favoriteEventDao = db.favoriteEventDao()
    }

    suspend fun getAllFavoritesSync(): List<FavoriteEvent> {
        return withContext(Dispatchers.IO) {
            favoriteEventDao.getAllFavorites()
        }
    }

    suspend fun isFavoriteSync(eventId: Int): FavoriteEvent? {
        return withContext(Dispatchers.IO) {
            favoriteEventDao.isFavorite(eventId)
        }
    }

    suspend fun addFavorite(event: FavoriteEvent) {
        withContext(Dispatchers.IO) {
            favoriteEventDao.addFavorite(event)
        }
    }

    suspend fun removeFavorite(eventId: Int) {
        withContext(Dispatchers.IO) {
            favoriteEventDao.removeFavoriteById(eventId)
        }
    }
}
