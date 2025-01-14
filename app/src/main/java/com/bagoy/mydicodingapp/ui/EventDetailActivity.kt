package com.bagoy.mydicodingapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bagoy.mydicodingapp.R
import com.bagoy.mydicodingapp.data.database.FavoriteEvent
import com.bagoy.mydicodingapp.data.network.ApiConfig
import com.bagoy.mydicodingapp.data.repository.FavoriteEventRepository
import com.bagoy.mydicodingapp.data.response.ListEventsItem
import com.bagoy.mydicodingapp.databinding.ActivityEventDetailBinding
import com.bagoy.mydicodingapp.ui.favorite.FavoriteViewModel
import com.bagoy.mydicodingapp.ui.viewmodel.FavoriteEventViewModelFactory
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventDetailBinding
    private val favoriteEventRepository: FavoriteEventRepository by lazy { FavoriteEventRepository(application) }
    private val favoriteViewModel: FavoriteViewModel by viewModels {
        FavoriteEventViewModelFactory(favoriteEventRepository)
    }

    @SuppressLint("ResourceAsColor", "Deprecated")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@EventDetailActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                finish()
            }
        })

        val eventId = intent.getIntExtra("event_id", -1)
        if (eventId != -1) {
            Log.d("EventDetailActivity", "Loading event from notification: ID = $eventId")
            fetchEventDetailFromApi(eventId)
            return
        }

        val event: ListEventsItem? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("event", ListEventsItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("event")
        }

        val favoriteEvent: FavoriteEvent? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("favoriteEvent", FavoriteEvent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("favoriteEvent")
        }

        when {
            event != null -> {
                Log.d("EventDetailActivity", "Loading event from intent: ${event.name}, ID: ${event.id}")
                displayEventFromApi(event)
            }
            favoriteEvent != null -> {
                Log.d("EventDetailActivity", "Loading event from favorites: ${favoriteEvent.name}")
                displayEventFromFavorite(favoriteEvent)
            }
            else -> {
                Toast.makeText(this, "Data acara tidak ditemukan", Toast.LENGTH_SHORT).show()

                finish()
            }
        }
    }

    private fun fetchEventDetailFromApi(eventId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiConfig.getApiService().getLatestEvent(eventId)
                withContext(Dispatchers.Main) {
                    if (response.error == false && response.listEvents.isNotEmpty()) {
                        displayEventFromApi(response.listEvents.first())
                    } else {
                        Toast.makeText(this@EventDetailActivity, "Data acara tidak ditemukan", Toast.LENGTH_SHORT).show()
                        Log.d("EventDetailActivity", "Load event from notification: ID = $eventId")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EventDetailActivity, "Gagal mengambil data acara", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun updateSaveButtonState(isFavorite: Boolean) {
        with(binding.saveButton) {
            isEnabled = true
            text = if (isFavorite) getString(R.string.saved) else getString(R.string.save_to_favorite)
            setBackgroundColor(
                if (isFavorite) getColor(com.google.android.material.R.color.design_default_color_secondary_variant)
                else getColor(com.google.android.material.R.color.design_default_color_secondary)
            )

            setTextColor(
                if (isFavorite) getColor(android.R.color.white)
                else getColor(android.R.color.black)
            )
        }
    }

    private fun displayEventFromApi(event: ListEventsItem) {
        supportActionBar?.title = getString(R.string.detail)
        with(binding) {
            eventTitle.text = getString(R.string.event_title, event.name)
            eventDescription.text = Html.fromHtml(removeImgTags(event.description), Html.FROM_HTML_MODE_LEGACY)
            eventTime.text = getString(R.string.event_time, event.beginTime, event.endTime)
            eventLocation.text = getString(R.string.event_location, event.cityName)
            eventOwnerName.text = getString(R.string.event_owner_name, event.ownerName)
            val remainingQuota = event.quota - event.registrants
            eventQuota.text = getString(R.string.event_remaining_quota, remainingQuota)
            eventCategory.text = getString(R.string.event_category, event.category)
            eventSummary.text = getString(R.string.event_summary, event.summary)

            Glide.with(this@EventDetailActivity)
                .load(event.mediaCover)
                .into(eventCoverImageView)

            favoriteViewModel.checkIsFavorite(event.id)
            favoriteViewModel.isFavorite.observe(this@EventDetailActivity) { isFavorite ->
                updateSaveButtonState(isFavorite)
            }

            saveButton.setOnClickListener {
                if (favoriteViewModel.isFavorite.value == true) {
                    favoriteViewModel.removeFavorite(event.id)
                    Toast.makeText(this@EventDetailActivity, "Event removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    val favoriteEvent = FavoriteEvent(
                        id = event.id,
                        name = event.name,
                        summary = event.summary,
                        description = event.description,
                        imageLogo = event.imageLogo,
                        mediaCover = event.mediaCover,
                        category = event.category,
                        ownerName = event.ownerName,
                        cityName = event.cityName,
                        quota = event.quota,
                        registrants = event.registrants,
                        beginTime = event.beginTime,
                        endTime = event.endTime,
                        link = event.link
                    )
                    favoriteViewModel.addFavorite(favoriteEvent)
                    Toast.makeText(this@EventDetailActivity, "Event saved to favorites", Toast.LENGTH_SHORT).show()
                }
                favoriteViewModel.checkIsFavorite(event.id)
            }
            registerButton.setOnClickListener {
                val eventUrl = event.link
                if (eventUrl.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(eventUrl))
                    startActivity(intent)
                }
            }
        }
    }

    private fun displayEventFromFavorite(favoriteEvent: FavoriteEvent) {
        supportActionBar?.title = getString(R.string.detail)
        with(binding) {
            eventTitle.text = getString(R.string.event_title, favoriteEvent.name)
            eventDescription.text = Html.fromHtml(removeImgTags(favoriteEvent.description), Html.FROM_HTML_MODE_LEGACY)
            eventTime.text = getString(R.string.event_time, favoriteEvent.beginTime, favoriteEvent.endTime)
            eventLocation.text = getString(R.string.event_location, favoriteEvent.cityName)
            eventOwnerName.text = getString(R.string.event_owner_name, favoriteEvent.ownerName)

            val remainingQuota = favoriteEvent.quota - favoriteEvent.registrants
            eventQuota.text = getString(R.string.event_remaining_quota, remainingQuota)
            eventCategory.text = getString(R.string.event_category, favoriteEvent.category)
            eventSummary.text = getString(R.string.event_summary, favoriteEvent.summary)
            Glide.with(this@EventDetailActivity)
                .load(favoriteEvent.mediaCover)
                .into(eventCoverImageView)

            favoriteViewModel.checkIsFavorite(favoriteEvent.id)
            favoriteViewModel.isFavorite.observe(this@EventDetailActivity) { isFavorite ->
                updateSaveButtonState(isFavorite)
            }

            saveButton.setOnClickListener {
                favoriteViewModel.removeFavorite(favoriteEvent.id)
                Toast.makeText(this@EventDetailActivity, "Event removed from favorites", Toast.LENGTH_SHORT).show()
            }

            registerButton.setOnClickListener {
                val eventUrl = favoriteEvent.link
                if (eventUrl.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(eventUrl))
                    startActivity(intent)
                }
            }
        }
    }

    private fun removeImgTags(html: String): String {
        return html.replace(Regex("<img[^>]*>"), "")
    }

}