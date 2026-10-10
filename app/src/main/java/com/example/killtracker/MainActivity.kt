package com.example.killtracker

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
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
            setPadding(0, 0, 0, 10)
        }
        layout.addView(title)

        val clearButton = Button(this).apply {
            text = "Xóa lịch sử Log"
            setOnClickListener {
                LogStore.clear(this@MainActivity)
                updateLogDisplay()
            }
        }
        layout.addView(clearButton)

        logTextView = TextView(this).apply {
            text = "Đang tải dữ liệu...\n"
            textSize = 14f
            setTextColor(Color.DKGRAY)
            setPadding(0, 20, 0, 0)
        }

        val scrollView = ScrollView(this).apply {
            addView(logTextView)
        }
        layout.addView(scrollView)

        setContentView(layout)

        // Ghi nhận mốc Activity khởi chạy lại
        LogStore.add(applicationContext, "System: Tiến trình MainActivity vừa được mở/khôi phục.")

        LogStore.onNewLog = {
            runOnUiThread { updateLogDisplay() }
        }

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
            LogStore.add(applicationContext, "Lỗi kích hoạt Service: ${e.message}")
        }
    }

    private fun updateLogDisplay() {
        val logs = LogStore.getLogs(this)
        val sb = StringBuilder()
        if (logs.isEmpty()) {
            sb.append("Chưa có mốc sự kiện nào.")
        } else {
            for (log in logs) {
                sb.append(log).append("\n\n")
            }
        }
        logTextView.text = sb.toString()
    }
}
