package com.bagoy.mydicodingapp.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagoy.mydicodingapp.data.repository.EventRepository
import com.bagoy.mydicodingapp.data.response.ListEventsItem
import kotlinx.coroutines.launch

class FinishedViewModel(private val repository: EventRepository) : ViewModel() {

    private val _pastEvents = MutableLiveData<List<ListEventsItem>>()
    val pastEvents: LiveData<List<ListEventsItem>> get() = _pastEvents

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun loadPastEvents() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val events = repository.getPastEvents()
                _pastEvents.value = events
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
                _pastEvents.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}

