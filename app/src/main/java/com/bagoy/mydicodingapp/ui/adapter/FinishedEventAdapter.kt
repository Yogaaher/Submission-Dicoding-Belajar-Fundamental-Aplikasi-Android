package com.bagoy.mydicodingapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bagoy.mydicodingapp.R
import com.bagoy.mydicodingapp.data.response.ListEventsItem
import com.bagoy.mydicodingapp.databinding.ItemEventBinding
import com.bagoy.mydicodingapp.ui.EventDetailActivity
import com.bumptech.glide.Glide

class FinishedEventAdapter(private var events: List<ListEventsItem>) : RecyclerView.Adapter<FinishedEventAdapter.EventViewHolder>() {
    class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem?) {
            if (event != null) {
                binding.eventTitle.text = binding.root.context.getString(R.string.event_title, event.name)
                binding.eventCategory.text = binding.root.context.getString(R.string.event_category, event.category)
                binding.eventSummary.text = binding.root.context.getString(R.string.event_summary, event.summary)
                Glide.with(binding.root.context)
                    .load(event.imageLogo)
                    .into(binding.eventImageView)
                binding.eventImageView.contentDescription = event.name
                binding.root.setOnClickListener {
                    val intent = Intent(binding.root.context, EventDetailActivity::class.java).apply {
                        putExtra("event", event)
                    }
                    binding.root.context.startActivity(intent)
                }
            } else {
                binding.eventTitle.text = binding.root.context.getString(R.string.event_title, "No Title")
                binding.eventCategory.text = ""
                binding.eventSummary.text = ""
                binding.eventImageView.setImageResource(0)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateEvents(newEvents: List<ListEventsItem>) {
        events = newEvents
        notifyDataSetChanged()
    }
}
