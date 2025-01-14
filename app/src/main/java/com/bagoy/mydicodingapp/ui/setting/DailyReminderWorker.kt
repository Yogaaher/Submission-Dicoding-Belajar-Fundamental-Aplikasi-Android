package com.bagoy.mydicodingapp.ui.setting

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bagoy.mydicodingapp.R
import com.bagoy.mydicodingapp.data.network.ApiConfig
import com.bagoy.mydicodingapp.data.response.Response
import com.bagoy.mydicodingapp.ui.EventDetailActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import androidx.work.Constraints

class DailyReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isNotificationEnabled = sharedPreferences.getBoolean("notification_enabled", true)

        if (isNotificationEnabled) {
            fetchLatestEvent()
            Log.e("DailyReminderWorker", "fetch success")
            return Result.success()
        } else {
            Log.e("DailyReminderWorker", "Notification is disabled by user.")
            return Result.failure()
        }
    }

    private fun fetchLatestEvent() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = ApiConfig.getApiService()
                val response: Response = apiService.getLatestEvent()
                Log.d("DailyReminderWorker", "API Response: $response")

                if (response.error == true) {
                    Log.e("DailyReminderWorker", "Error fetching latest event: ${response.message}")
                    return@launch
                }

                if (response.listEvents.isNotEmpty()) {
                    val event = response.listEvents[0]
                    val title = event.name
                    val message = "Akan dilaksanakan: ${event.beginTime}"
                    sendNotification(applicationContext, title, message, event.id)
                } else {
                    Log.d("DailyReminderWorker", "No events found.")
                }
            } catch (e: HttpException) {
                Log.e("DailyReminderWorker", "Error fetching latest event: ${e.message()}")
            } catch (e: Exception) {
                Log.e("DailyReminderWorker", "Network failure: ${e.message}")
            }
        }
    }

    private fun sendNotification(context: Context, title: String, message: String, eventId: Int) {
        val channelId = "event_reminder_channel"
        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Event Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        Log.d("DailyReminderWorker", "Sending notification for Event ID: $eventId")

        val intent = Intent(context, EventDetailActivity::class.java).apply {
            putExtra("event_id", eventId)
        }

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(eventId, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(eventId, notification)
        } else {
            Log.e("DailyReminderWorker", "Cannot send notification, permission not granted.")
        }
    }

    companion object {
        fun scheduleWork(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build())
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "DailyReminderWork",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
    }
}
