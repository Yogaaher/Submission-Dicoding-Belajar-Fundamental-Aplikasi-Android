package com.bagoy.mydicodingapp.data.network

import com.bagoy.mydicodingapp.data.response.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @GET("events")
    suspend fun getUpcomingEvents(
        @Query("active") active: Int = 1
    ): Response

    @GET("events")
    suspend fun getPastEvents(
        @Query("active") active: Int = 0
    ): Response

    @GET("events/{id}")
    suspend fun getEventDetail(
        @Path("id") id: Int
    ): Response

    @GET("events")
    suspend fun getLatestEvent(
        @Query("active") active: Int = -1,
        @Query("limit") limit: Int = 1
    ): Response
}


