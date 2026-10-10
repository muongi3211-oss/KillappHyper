package com.example.killtracker

object LogStore {
    val logs = mutableListOf<String>()
    var onNewLog: (() -> Unit)? = null

    fun add(message: String) {
        val time = android.text.format.DateFormat.format("HH:mm:ss", System.currentTimeMillis())
        logs.add(0, "[$time] $message") // Thêm lên đầu danh sách
        if (logs.size > 30) logs.removeAt(logs.size - 1) // Giới hạn 30 dòng gần nhất
        onNewLog?.invoke()
    }
}
