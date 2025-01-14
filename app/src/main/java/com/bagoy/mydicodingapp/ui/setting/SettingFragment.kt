package com.bagoy.mydicodingapp.ui.setting

import android.app.NotificationManager
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bagoy.mydicodingapp.R
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var switchDarkMode: SwitchCompat
    private lateinit var switchNotification: SwitchCompat
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("settings", 0)
        notificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    setupDailyReminderWork()
                    Toast.makeText(requireContext(), "Izin notifikasi diberikan.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Izin notifikasi diperlukan untuk menggunakan fitur ini",
                        Toast.LENGTH_SHORT
                    ).show()
                    switchNotification.isChecked = false
                }
            }

        val isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        switchDarkMode = view.findViewById(R.id.switch_dark_mode)
        switchNotification = view.findViewById(R.id.switch_notifications)
        setInitialSwitchStates()
        setupSwitchListeners()
        return view
    }

    private fun setInitialSwitchStates() {
        switchDarkMode.isChecked = sharedPreferences.getBoolean("dark_mode", false)
        switchNotification.isChecked = sharedPreferences.getBoolean("notifications", true)

        Log.d("SettingFragment", "Switch Notification is checked: ${switchNotification.isChecked}")
    }

    private fun clearNotifications() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupSwitchListeners() {
        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            Log.d("SettingFragment", "Switch Notification is checked: $isChecked")
            with(sharedPreferences.edit()) {
                putBoolean("notifications", isChecked)
                apply()
            }

            if (isChecked) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestNotificationPermission()
                } else {
                    setupDailyReminderWork()
                }
            } else {
                WorkManager.getInstance(requireContext()).cancelUniqueWork("dailyReminderWork")
                clearNotifications()
            }
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            Log.d("SettingFragment", "Switch Dark Mode is checked: $isChecked")
            with(sharedPreferences.edit()) {
                putBoolean("dark_mode", isChecked)
                apply()
            }

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun setupDailyReminderWork() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "dailyReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
