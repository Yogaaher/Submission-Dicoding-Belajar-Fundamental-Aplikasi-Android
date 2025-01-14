package com.bagoy.mydicodingapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bagoy.mydicodingapp.data.response.ListEventsItem
import com.bagoy.mydicodingapp.databinding.ItemEventBinding
import com.bagoy.mydicodingapp.databinding.ItemUpcomingEventBinding
import com.bagoy.mydicodingapp.ui.EventDetailActivity
import com.bumptech.glide.Glide

class UpcomingEventAdapter(
    private var events: List<ListEventsItem>,
    private val isHorizontal: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class VerticalViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.eventTitle.text = event.name
            binding.eventCategory.text = event.category
            binding.eventSummary.text = event.summary
            binding.eventSummary.text = event.summary

            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .into(binding.eventImageView)

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, EventDetailActivity::class.java).apply {
                    putExtra("event", event)
                }
                binding.root.context.startActivity(intent)
            }
        }
    }

    inner class HorizontalViewHolder(private val binding: ItemUpcomingEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.upcomingEventTitle.text = event.name
            binding.upcomingEventCategory.text = event.category

            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .into(binding.eventImageView)

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, EventDetailActivity::class.java).apply {
                    putExtra("event", event)
                }
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isHorizontal) {
            val binding = ItemUpcomingEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HorizontalViewHolder(binding)
        } else {
            val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            VerticalViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = events[position]
        if (holder is HorizontalViewHolder) {
            holder.bind(event)
        } else if (holder is VerticalViewHolder) {
            holder.bind(event)
        }
    }

    override fun getItemCount(): Int = events.size
    @SuppressLint("NotifyDataSetChanged")
    fun updateEvents(newEvents: List<ListEventsItem>) {
        events = newEvents
        notifyDataSetChanged()
    }
}
