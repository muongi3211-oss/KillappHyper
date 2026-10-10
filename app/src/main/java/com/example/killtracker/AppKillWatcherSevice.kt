package com.example.killtracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class AppKillWatcherService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("KillTracker", "Service initialized")
        startMyForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.w("KillTracker", "WARNING: App removed from Recents")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Log.e("KillTracker", "CRITICAL: Process destroyed/killed by system")
        super.onDestroy()
    }

    private fun startMyForeground() {
        try {
            val channelId = "kill_tracker_channel"
            val notificationManager = getSystemService(NotificationManager::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Kill Tracker Monitor",
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationManager?.createNotificationChannel(channel)
            }

            val notification: Notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Kill Tracker đang chạy")
                .setContentText("Đang giám sát tiến trình ngầm...")
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .build()

            startForeground(1, notification)
        } catch (e: Exception) {
            Log.e("KillTracker", "Error starting foreground: ${e.message}")
        }
    }
}
