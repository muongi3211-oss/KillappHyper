package com.example.killtracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.concurrent.Executors

class AppKillWatcherService : Service() {

    // Sử dụng SingleThreadExecutor để xử lý công việc ngầm hoàn toàn độc lập với Main Thread
    private val executor = Executors.newSingleThreadExecutor()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Đưa việc tạo Notification và khởi chạy Foreground sang luồng phụ
        executor.execute {
            try {
                startMyForeground()
            } catch (e: Exception) {
                Log.e("KillTracker", "Error in background execution: ${e.message}")
            }
        }
        
        // Trả về START_STICKY ngay lập tức để giải phóng Main Thread cho hệ thống
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        executor.execute {
            LogStore.add("CẢNH BÁO: App bị vuốt khỏi đa nhiệm (Recents)!")
        }
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        executor.execute {
            LogStore.add("NGUY HIỂM: Process bị hệ thống Kill!")
        }
        executor.shutdown()
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
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE)
            } else {
                startForeground(1, notification)
            }
        } catch (e: Exception) {
            Log.e("KillTracker", "Error starting foreground: ${e.message}")
        }
    }
}
