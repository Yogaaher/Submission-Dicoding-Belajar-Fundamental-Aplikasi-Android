package com.bagoy.mydicodingapp.ui.home
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagoy.mydicodingapp.data.repository.EventRepository
import com.bagoy.mydicodingapp.data.response.ListEventsItem
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: EventRepository) : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> get() = _upcomingEvents

    private val _pastEvents = MutableLiveData<List<ListEventsItem>>()
    val pastEvents: LiveData<List<ListEventsItem>> get() = _pastEvents

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun loadUpcomingEvents() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val events = repository.getUpcomingEvents()
                _upcomingEvents.value = events
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading upcoming events: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadPastEvents() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val events = repository.getPastEvents()
                _pastEvents.value = events
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading past events: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }


}