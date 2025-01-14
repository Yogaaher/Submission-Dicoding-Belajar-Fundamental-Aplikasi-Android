package com.bagoy.mydicodingapp.data.repository

import com.bagoy.mydicodingapp.data.network.ApiService
import com.bagoy.mydicodingapp.data.response.ListEventsItem

class EventRepository(private val apiService: ApiService) {

    suspend fun getUpcomingEvents(): List<ListEventsItem> {
        return try {
            val response = apiService.getUpcomingEvents()
            if (response.error == false) {
                response.listEvents
            } else {
                throw Exception(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            throw Exception(e.message ?: "Unknown error")
        }
    }

    suspend fun getPastEvents(): List<ListEventsItem> {
        return try {
            val response = apiService.getPastEvents()
            if (response.error == false) {
                response.listEvents
            } else {
                throw Exception(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            throw Exception(e.message ?: "Unknown error")
        }
    }
}