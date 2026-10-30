package com.kinh.appkiller

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import androidx.core.app.NotificationCompat

class AppMonitorService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private var targetGamePackage = "com.vng.pubgmobile"
    private var isTriggered = false

    private val monitorRunnable = object : Runnable {
        override fun run() {
            checkForegroundApp()
            handler.postDelayed(this, 1500) // Kiểm tra mỗi 1.5 giây
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra("GAME_PACKAGE")?.let {
            if (it.isNotEmpty()) targetGamePackage = it
        }

        startForeground(1, createNotification())
        handler.post(monitorRunnable)
        return START_STICKY
    }

    private fun checkForegroundApp() {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            time - 1000 * 10,
            time
        )

        if (!stats.isNullOrEmpty()) {
            val sortedStats = stats.sortedBy { it.lastTimeUsed }
            val currentApp = sortedStats.lastOrNull()?.packageName

            if (currentApp == targetGamePackage) {
                if (!isTriggered) {
                    isTriggered = true
                    // Mở App Info của Battery and performance (PowerKeeper)
                    openAppInfo("com.miui.powerkeeper")
                    // Mở App Info của Joyose
                    openAppInfo("com.xiaomi.joyose")
                }
            } else if (currentApp != null && 
                       currentApp != "com.miui.powerkeeper" && 
                       currentApp != "com.xiaomi.joyose" && 
                       currentApp != packageName) {
                // Reset trạng thái khi bạn thoát game để sẵn sàng cho lần mở game tiếp theo
                isTriggered = false
            }
        }
    }

    private fun openAppInfo(pkg: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$pkg")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotification(): Notification {
        val channelId = "AppMonitorChannel"
        val channel = NotificationChannel(
            channelId,
            "Trợ lý GMS Protect",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Đang giám sát Game...")
            .setContentText("Sẵn sàng trượt mở Cài đặt pin/joyose khi vào game")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        handler.removeCallbacks(monitorRunnable)
        super.onDestroy()
    }
}
