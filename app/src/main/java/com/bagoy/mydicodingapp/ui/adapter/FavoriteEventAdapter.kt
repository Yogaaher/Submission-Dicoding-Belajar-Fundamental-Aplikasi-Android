package com.bagoy.mydicodingapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bagoy.mydicodingapp.R
import com.bagoy.mydicodingapp.data.database.FavoriteEvent
import com.bagoy.mydicodingapp.databinding.ItemEventBinding
import com.bumptech.glide.Glide

class FavoriteEventAdapter(
    private var favorites: List<FavoriteEvent>,
    private val onItemClick: (FavoriteEvent) -> Unit
) : RecyclerView.Adapter<FavoriteEventAdapter.FavoriteEventViewHolder>() {
    class FavoriteEventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: FavoriteEvent, onItemClick: (FavoriteEvent) -> Unit) {
            binding.eventTitle.text = binding.root.context.getString(R.string.event_title, favorite.name)
            binding.eventCategory.text = binding.root.context.getString(R.string.event_category, favorite.category)
            binding.eventSummary.text = binding.root.context.getString(R.string.event_summary, favorite.summary)
            Glide.with(binding.root.context)
                .load(favorite.imageLogo)
                .into(binding.eventImageView)
            binding.eventImageView.contentDescription = favorite.name
            binding.root.setOnClickListener {
                onItemClick(favorite)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteEventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteEventViewHolder, position: Int) {
        holder.bind(favorites[position]) { favoriteEvent ->
            onItemClick(favoriteEvent) }
    }

    override fun getItemCount(): Int = favorites.size
    @SuppressLint("NotifyDataSetChanged")
    fun updateFavorites(newFavorites: List<FavoriteEvent>) {
        favorites = newFavorites
        notifyDataSetChanged()
    }
}
