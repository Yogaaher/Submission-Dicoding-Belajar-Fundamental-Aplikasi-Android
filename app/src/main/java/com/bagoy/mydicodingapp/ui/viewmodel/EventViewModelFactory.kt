package com.bagoy.mydicodingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bagoy.mydicodingapp.data.repository.EventRepository
import com.bagoy.mydicodingapp.ui.finished.FinishedViewModel
import com.bagoy.mydicodingapp.ui.home.HomeViewModel
import com.bagoy.mydicodingapp.ui.upcoming.UpcomingViewModel

class EventViewModelFactory(
    private val eventRepository: EventRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UpcomingViewModel::class.java) -> {
                UpcomingViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(FinishedViewModel::class.java) -> {
                FinishedViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(eventRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
