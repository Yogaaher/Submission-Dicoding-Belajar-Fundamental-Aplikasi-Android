package com.bagoy.mydicodingapp.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagoy.mydicodingapp.R
import com.bagoy.mydicodingapp.data.network.ApiConfig
import com.bagoy.mydicodingapp.data.repository.EventRepository
import com.bagoy.mydicodingapp.databinding.FragmentHomeBinding
import com.bagoy.mydicodingapp.ui.adapter.FinishedEventAdapter
import com.bagoy.mydicodingapp.ui.adapter.UpcomingEventAdapter
import com.bagoy.mydicodingapp.ui.viewmodel.EventViewModelFactory

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterUpcoming: UpcomingEventAdapter
    private lateinit var adapterFinished: FinishedEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = ApiConfig.getApiService()
        val repository = EventRepository(apiService)
        homeViewModel = ViewModelProvider(this, EventViewModelFactory(repository))[HomeViewModel::class.java]

        adapterUpcoming = UpcomingEventAdapter(emptyList(), true)
        adapterFinished = FinishedEventAdapter(emptyList())

        binding.recyclerViewUpcomingEvents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewUpcomingEvents.adapter = adapterUpcoming

        binding.recyclerViewFinishedEvents.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewFinishedEvents.adapter = adapterFinished

        if (!isNetworkAvailable()) {
            binding.textViewNoEvents.text = getString(R.string.offline_message)
            binding.textViewNoEvents2.text = getString(R.string.offline_message)
            binding.textViewNoEvents.visibility = View.VISIBLE
            binding.textViewNoEvents2.visibility = View.VISIBLE
            return
        }

        homeViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.loadUpcomingEvents()
        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            val upcomingList = events?.take(5) ?: emptyList()
            adapterUpcoming.updateEvents(upcomingList)
            binding.textViewNoEvents2.visibility = if (upcomingList.isEmpty()) View.VISIBLE else View.GONE
        }

        homeViewModel.loadPastEvents()
        homeViewModel.pastEvents.observe(viewLifecycleOwner) { events ->
            val finishedList = events?.take(5) ?: emptyList()
            adapterFinished.updateEvents(finishedList)
            binding.textViewNoEvents.visibility = if (finishedList.isEmpty()) View.VISIBLE else View.GONE
        }

        setupSearchView()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { performSearch(it) }
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredUpcomingEvents = homeViewModel.upcomingEvents.value?.filter { event ->
            event.name.contains(query, ignoreCase = true)
        }
        adapterUpcoming.updateEvents(filteredUpcomingEvents ?: emptyList())
        binding.textViewNoEvents2.visibility = if (filteredUpcomingEvents.isNullOrEmpty()) View.VISIBLE else View.GONE

        val filteredFinishedEvents = homeViewModel.pastEvents.value?.filter { event ->
            event.name.contains(query, ignoreCase = true)
        }
        adapterFinished.updateEvents(filteredFinishedEvents ?: emptyList())
        binding.textViewNoEvents.visibility = if (filteredFinishedEvents.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
