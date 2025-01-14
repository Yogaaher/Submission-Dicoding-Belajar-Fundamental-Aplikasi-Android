package com.bagoy.mydicodingapp.ui.finished

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.bagoy.mydicodingapp.data.repository.EventRepository
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagoy.mydicodingapp.R
import com.bagoy.mydicodingapp.data.network.ApiConfig
import com.bagoy.mydicodingapp.databinding.FragmentFinishedBinding
import com.bagoy.mydicodingapp.ui.adapter.FinishedEventAdapter
import com.bagoy.mydicodingapp.ui.viewmodel.EventViewModelFactory

class FinishedFragment : Fragment() {
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventViewModel: FinishedViewModel
    private lateinit var adapterFinished: FinishedEventAdapter
    private lateinit var textViewNoEvents: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val apiService = ApiConfig.getApiService()
        val repository = EventRepository(apiService)

        eventViewModel = ViewModelProvider(this, EventViewModelFactory(repository))[FinishedViewModel::class.java]
        binding.recyclerViewFinished.layoutManager = LinearLayoutManager(context)
        adapterFinished = FinishedEventAdapter(emptyList())
        binding.recyclerViewFinished.adapter = adapterFinished

        val bottomNavigationViewHeight = 56
        val density = resources.displayMetrics.density
        binding.recyclerViewFinished.setPadding(0, 0, 0, (bottomNavigationViewHeight * density).toInt())

        textViewNoEvents = binding.textViewNoEvents
        eventViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        eventViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                eventViewModel.clearError()
            }
        }

        if (isInternetAvailable()) {
            eventViewModel.loadPastEvents()
            eventViewModel.pastEvents.observe(viewLifecycleOwner) { events ->
                val finishedList = events ?: emptyList()
                adapterFinished.updateEvents(finishedList)
                textViewNoEvents.visibility = if (finishedList.isEmpty()) View.VISIBLE else View.GONE
            }
        } else {
            textViewNoEvents.text = getString(R.string.offline_message)
            textViewNoEvents.visibility = View.VISIBLE
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
