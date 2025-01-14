package com.bagoy.mydicodingapp.ui.upcoming

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagoy.mydicodingapp.R
import com.bagoy.mydicodingapp.data.network.ApiConfig
import com.bagoy.mydicodingapp.data.repository.EventRepository
import com.bagoy.mydicodingapp.databinding.FragmentUpcomingBinding
import com.bagoy.mydicodingapp.ui.adapter.UpcomingEventAdapter
import com.bagoy.mydicodingapp.ui.viewmodel.EventViewModelFactory

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private lateinit var upcomingViewModel: UpcomingViewModel
    private lateinit var adapterUpcoming: UpcomingEventAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewNoEvents: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = ApiConfig.getApiService()
        val repository = EventRepository(apiService)

        upcomingViewModel = ViewModelProvider(this, EventViewModelFactory(repository))[UpcomingViewModel::class.java]
        binding.recyclerViewUpcoming.layoutManager = LinearLayoutManager(context)
        adapterUpcoming = UpcomingEventAdapter(emptyList(), false)
        binding.recyclerViewUpcoming.adapter = adapterUpcoming

        val bottomNavigationViewHeight = 56
        val density = resources.displayMetrics.density
        binding.recyclerViewUpcoming.setPadding(0, 0, 0, (bottomNavigationViewHeight * density).toInt())

        progressBar = binding.progressBar
        textViewNoEvents = binding.textViewNoEvents

        if (!isNetworkAvailable()) {
            textViewNoEvents.text = getString(R.string.offline_message)
            textViewNoEvents.visibility = View.VISIBLE
            return
        }

        upcomingViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        upcomingViewModel.loadUpcomingEvents()
        upcomingViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            val upcomingList = events?.take(5) ?: emptyList()
            adapterUpcoming.updateEvents(upcomingList)
            textViewNoEvents.visibility = if (upcomingList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
