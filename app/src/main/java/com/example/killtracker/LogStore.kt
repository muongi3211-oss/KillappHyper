package com.example.killtracker

import android.content.Context
import android.text.format.DateFormat

object LogStore {
    private const val PREF_NAME = "kill_tracker_logs"
    private const val KEY_LOGS = "logs_history"
    
    var onNewLog: (() -> Unit)? = null

    // Đọc lịch sử log đã lưu từ đĩa
    fun getLogs(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val savedString = prefs.getString(KEY_LOGS, "") ?: ""
        return if (savedString.isEmpty()) emptyList() else savedString.split("\n---LOG_SEP---\n")
    }

    // Ghi nhận log mới và lưu ngay vào bộ nhớ
    fun add(context: Context, message: String) {
        val time = DateFormat.format("HH:mm:ss (dd/MM)", System.currentTimeMillis())
        val newLog = "[$time] $message"
        
        val currentLogs = getLogs(context).toMutableList()
        currentLogs.add(0, newLog) // Thêm log mới lên đầu
        
        // Giữ tối đa 50 dòng log gần nhất
        if (currentLogs.size > 50) {
            currentLogs.removeAt(currentLogs.size - 1)
        }

        // Lưu danh sách vào SharedPreferences
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LOGS, currentLogs.joinToString("\n---LOG_SEP---\n")).apply()

        onNewLog?.invoke()
    }

    // Nút xóa lịch sử nếu cần
    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        onNewLog?.invoke()
    }
}
