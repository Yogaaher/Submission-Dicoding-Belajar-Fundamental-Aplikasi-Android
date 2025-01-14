package com.bagoy.mydicodingapp.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagoy.mydicodingapp.data.repository.EventRepository
import com.bagoy.mydicodingapp.data.response.ListEventsItem
import kotlinx.coroutines.launch

class UpcomingViewModel(private val repository: EventRepository) : ViewModel() {
    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> get() = _upcomingEvents

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>()

    fun loadUpcomingEvents() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val events = repository.getUpcomingEvents()
                _upcomingEvents.value = events
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message ?: "Unknown error"}"
            } finally {
                _loading.value = false
            }
        }
    }
}