package com.example.killtracker

import android.content.Intent
import android.graphics.Color
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
            text = "Đang tải dữ liệu...\n"
            textSize = 14f
            setTextColor(Color.DKGRAY)
        }

        val scrollView = ScrollView(this).apply {
            addView(logTextView)
        }
        layout.addView(scrollView)

        setContentView(layout)

        // Ghi nhận mốc ứng dụng vừa mở hoặc vừa được hệ thống khôi phục sau khi bị Kill
        LogStore.add("System: Tiến trình MainActivity vừa được khởi chạy lại.")

        // Lắng nghe sự kiện ghi log mới
        LogStore.onNewLog = {
            runOnUiThread { updateLogDisplay() }
        }

        // Tự động khởi chạy lại Service ngay khi Activity tải xong
        startTrackerService()
    }

    override fun onResume() {
        super.onResume()
        updateLogDisplay()
    }

    private fun startTrackerService() {
        try {
            val intent = Intent(this, AppKillWatcherService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            LogStore.add("Lỗi kích hoạt Service: ${e.message}")
        }
    }

    private fun updateLogDisplay() {
        val sb = StringBuilder()
        if (LogStore.logs.isEmpty()) {
            sb.append("Chưa có mốc sự kiện nào.")
        } else {
            for (log in LogStore.logs) {
                sb.append(log).append("\n\n")
            }
        }
        logTextView.text = sb.toString()
    }
}
