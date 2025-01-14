package com.bagoy.mydicodingapp.data.database

import androidx.room.*

@Dao
interface FavoriteEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(event: FavoriteEvent)

    @Delete
    suspend fun removeFavorite(event: FavoriteEvent)

    @Query("DELETE FROM favorite_events WHERE id = :eventId")
    suspend fun removeFavoriteById(eventId: Int)

    @Query("SELECT * FROM favorite_events")
    suspend fun getAllFavorites(): List<FavoriteEvent>

    @Query("SELECT * FROM favorite_events WHERE id = :eventId LIMIT 1")
    suspend fun isFavorite(eventId: Int): FavoriteEvent?
}
