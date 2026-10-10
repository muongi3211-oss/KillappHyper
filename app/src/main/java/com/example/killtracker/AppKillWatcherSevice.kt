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

    private var isRunning = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogStore.add("Service: onStartCommand đang chạy ngầm.")
        
        if (!isRunning) {
            isRunning = true
            Thread {
                try {
                    startMyForeground()
                    startHeartbeat()
                } catch (e: Exception) {
                    LogStore.add("Lỗi Service: ${e.message}")
                }
            }.start()
        }

        return START_STICKY
    }

    // Luồng ghi nhận nhịp tim định kỳ 60 giây một lần
    private fun startHeartbeat() {
        var count = 1
        while (isRunning) {
            try {
                Thread.sleep(60000) // Nghỉ 60 giây
                LogStore.add("Heartbeat #$count: Dịch vụ ngầm vẫn đang sống.")
                count++
            } catch (e: Exception) {
                break
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        LogStore.add("CẢNH BÁO: App bị vuốt khỏi đa nhiệm (Recents)!")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        isRunning = false
        LogStore.add("NGUY HIỂM: Process bị hệ thống Kill!")
        super.onDestroy()
    }

    private fun startMyForeground() {
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
        LogStore.add("Foreground: Dịch vụ nền đã khởi chạy thành công.")
    }
}
