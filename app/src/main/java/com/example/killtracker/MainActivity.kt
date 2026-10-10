package com.example.killtracker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var logTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tạo giao diện chứa khung hiển thị log trực tiếp
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 30, 30, 30)
        }

        val title = TextView(this).apply {
            text = "Lịch sử giám sát Kill Tracker"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 20)
        }
        layout.addView(title)

        logTextView = TextView(this).apply {
            text = "Đang chờ sự kiện...\n"
            textSize = 14f
            setTextColor(android.graphics.Color.DKGRAY)
        }

        val scrollView = ScrollView(this).apply {
            addView(logTextView)
        }
        layout.addView(scrollView)

        setContentView(layout)

        // Lắng nghe sự kiện log mới để cập nhật lên màn hình ngay lập tức
        LogStore.onNewLog = {
            runOnUiThread { updateLogDisplay() }
        }
        updateLogDisplay()

        // Khởi động dịch vụ nền
        try {
            val intent = Intent(this, AppKillWatcherService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            LogStore.add("Lỗi khởi động service: ${e.message}")
        }
    }

    private fun updateLogDisplay() {
        val sb = StringBuilder()
        for (log in LogStore.logs) {
            sb.append(log).append("\n\n")
        }
        logTextView.text = sb.toString()
    }
}
