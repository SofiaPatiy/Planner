package com.gmail.sofiapatiy.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gmail.sofiapatiy.MainActivity
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.ktx.asNormalizedDateTime
import com.gmail.sofiapatiy.repository.PlannerRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class CalendarForegroundService : Service() {

    @Inject
    internal lateinit var repository: PlannerRepository

    private val SERVICE_NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "CalendarForegroundService"
    private val CHANNEL_NAME = "Foreground Service Notifications"
    private val CHECK_INTERVAL_MINUTES = 1L

    private var serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onCreate() {
        super.onCreate()
        Log.d("CalendarForegroundService", "Service onCreate")
        createNotificationChannel() // Create channel for the ongoing notification

        serviceScope.launch {
            while (isActive) { // Check if the coroutine scope is still active
                repository.getAllTasks().firstOrNull()?.let { userTasks ->
                    val timeTick = LocalDateTime.now().asNormalizedDateTime()

                    // find first "deadlined" task for current timeTick
                    userTasks.firstOrNull { task ->
                        task.normalizedDeadlineDateTime == timeTick
                    }?.let { deadlinedTask ->

                        // inform, if any
                        sendLocalNotification(
                            "deadline for: ${deadlinedTask.name}",
                            deadlinedTask.formattedDeadlineDateTime
                        )
                    }
                }
                delay(CHECK_INTERVAL_MINUTES * 60 * 1000) // Suspend for 1 minute
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("CalendarForegroundService", "Service onStartCommand")

        val notification = createOngoingNotification()
        startForeground(SERVICE_NOTIFICATION_ID, notification)

        // Return START_STICKY, service to be restarted by the system
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // This service does not allow binding
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel() // Cancel all coroutines in the scope
        Log.d("CalendarForegroundService", "Service onDestroy")
    }

    // --- Notification Helper Methods ---

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW // Use IMPORTANCE_LOW for ongoing foreground service notification
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    private fun createOngoingNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntentFlags =
            PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for API 23+
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, pendingIntentFlags)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Planner Task Service")
            .setContentText("connected to server")
            .setSmallIcon(R.drawable.ic_baseline_calendar_month_24) // Use a small icon (replace with your icon)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Makes the notification non-dismissible
            .build()
    }

    // --- Logic for sending local notifications ---

    private val NOTIFICATION_CHANNEL_ID_ALERTS = "MyNotificationChannelAlerts"
    private val NOTIFICATION_CHANNEL_NAME_ALERTS = "App Alerts"
    private val NOTIFICATION_ID_ALERT = 2001 // Unique ID for your alert notifications

    private fun sendLocalNotification(title: String, message: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a separate channel for your actual alert notifications
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID_ALERTS,
            NOTIFICATION_CHANNEL_NAME_ALERTS,
            NotificationManager.IMPORTANCE_HIGH // Use HIGH for important alerts
        )
        notificationManager.createNotificationChannel(channel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntentFlags =
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent =
            PendingIntent.getActivity(this, 1, notificationIntent, pendingIntentFlags)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID_ALERTS)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp) // Use a different icon for alerts if desired
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Notification dismisses when tapped
            .build()

        notificationManager.notify(NOTIFICATION_ID_ALERT, notification)
        Log.d("CalendarForegroundService", "Sent local notification: $title - $message")
    }
}