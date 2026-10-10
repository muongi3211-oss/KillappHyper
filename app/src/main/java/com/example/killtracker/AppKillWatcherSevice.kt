package com.example.killtracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AppKillWatcherService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        LogStore.add("Service: onCreate đã gọi.")
    }

    // Chuyển việc khởi chạy dịch vụ nền vào onStartCommand để tránh nghẽn luồng chính (gây ANR)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogStore.add("Service: onStartCommand hoạt động.")
        startMyForeground()
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        LogStore.add("CẢNH BÁO: App bị vuốt khỏi đa nhiệm (Recents)!")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        LogStore.add("NGUY HIỂM: Process bị hệ thống Kill!")
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE)
            } else {
                startForeground(1, notification)
            }
            LogStore.add("Foreground: Khởi chạy thành công.")
        } catch (e: Exception) {
            LogStore.add("Lỗi Foreground: ${e.message}")
        }
    }
}
