package com.example.killtracker

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class AppKillWatcherService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("KillTracker", "Dịch vụ theo dõi đã khởi tạo: ${System.currentTimeMillis()}")
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Yêu cầu Android tự khởi chạy lại service nếu bị kill
    }

    // Được gọi khi người dùng vuốt bỏ app khỏi Màn hình đa nhiệm
    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.w("KillTracker", "CẢNH BÁO: App vừa bị hủy khỏi danh sách Đa nhiệm (Recents) lúc: ${System.currentTimeMillis()}")
        super.onTaskRemoved(rootIntent)
    }

    // Được gọi khi HyperOS diệt tiến trình này để giải phóng RAM/Pin
    override fun onDestroy() {
        Log.e("KillTracker", "CẢNH BÁO NGUY CẤP: HyperOS đã diệt (Kill) tiến trình này lúc: ${System.currentTimeMillis()}")
        super.onDestroy()
    }

    private fun startForegroundService() {
        val channelId = "kill_tracker_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Kill Tracker", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Kill Tracker đang chạy")
            .setContentText("Đang giám sát cơ chế kill app của HyperOS...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1, notification)
    }
}
