package com.example.killtracker

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

object UsageHelper {
    fun hasPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun requestPermission(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun getRecentEvents(context: Context): List<String> {
        val list = mutableListOf<String>()
        if (!hasPermission(context)) return listOf("Chưa cấp quyền Truy cập dữ liệu sử dụng!")

        try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                ?: return list

            val endTime = System.currentTimeMillis()
            val startTime = endTime - (1000 * 60 * 120) // Quét trong 2 tiếng gần nhất
            val events = usm.queryEvents(startTime, endTime)
            val event = UsageEvents.Event()

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_BACKGROUND ||
                    event.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
                    
                    val timeFormat = android.text.format.DateFormat.format("HH:mm:ss", event.timeStamp)
                    list.add(0, "[$timeFormat] App dừng: ${event.packageName}")
                }
            }
        } catch (e: Exception) {
            list.add("Lỗi đọc dữ liệu: ${e.message}")
        }

        return if (list.isEmpty()) listOf("Không có sự kiện đóng ứng dụng nào gần đây.") else list
    }
}
