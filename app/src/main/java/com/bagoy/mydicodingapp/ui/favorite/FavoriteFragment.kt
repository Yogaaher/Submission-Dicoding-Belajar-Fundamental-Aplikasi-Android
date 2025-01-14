package com.bagoy.mydicodingapp.ui.favorite

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagoy.mydicodingapp.data.repository.FavoriteEventRepository
import com.bagoy.mydicodingapp.databinding.FragmentFavoriteBinding
import com.bagoy.mydicodingapp.ui.adapter.FavoriteEventAdapter
import com.bagoy.mydicodingapp.data.database.FavoriteEvent
import com.bagoy.mydicodingapp.ui.EventDetailActivity
import com.bagoy.mydicodingapp.ui.viewmodel.FavoriteEventViewModelFactory

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var adapterFavorite: FavoriteEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = FavoriteEventRepository(requireActivity().application)
        favoriteViewModel = ViewModelProvider(this, FavoriteEventViewModelFactory(repository))[FavoriteViewModel::class.java]
        adapterFavorite = FavoriteEventAdapter(emptyList()) { favoriteEvent ->
            navigateToDetail(favoriteEvent)
        }
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewFavorites.adapter = adapterFavorite
        binding.textViewNoFavorites.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        favoriteViewModel.favoriteList.observe(viewLifecycleOwner) { favoriteEvents ->
            binding.progressBar.visibility = View.GONE
            if (favoriteEvents.isNullOrEmpty()) {
                binding.recyclerViewFavorites.visibility = View.GONE
                binding.textViewNoFavorites.visibility = View.VISIBLE
            } else {
                binding.recyclerViewFavorites.visibility = View.VISIBLE
                binding.textViewNoFavorites.visibility = View.GONE
                adapterFavorite.updateFavorites(favoriteEvents)
            }
        }

        favoriteViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        favoriteViewModel.loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        favoriteViewModel.loadFavorites()
    }

    private fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun navigateToDetail(favoriteEvent: FavoriteEvent) {
        if (isConnectedToInternet(requireContext())) {
            val intent = Intent(context, EventDetailActivity::class.java).apply {
                putExtra("favoriteEvent", favoriteEvent)
            }
            startActivity(intent)
        } else {
            Toast.makeText(context, "Harap terhubung ke internet untuk melihat detail acara", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
