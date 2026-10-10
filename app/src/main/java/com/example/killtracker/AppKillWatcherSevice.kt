package com.example.killtracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AppKillWatcherService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        LogStore.add("Service: Đã khởi động và bắt đầu giám sát.")
        startMyForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        LogStore.add("CẢNH BÁO: App bị vuốt khỏi màn hình gần đây (Recents)!")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        LogStore.add("NGUY HIỂM: Process bị hệ thống hoặc người dùng Kill!")
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
            LogStore.add("Lỗi Foreground: ${e.message}")
        }
    }
}
