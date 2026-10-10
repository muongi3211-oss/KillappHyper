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
    private lateinit var permissionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 30, 30, 30)
        }

        val title = TextView(this).apply {
            text = "Giám sát ứng dụng toàn hệ thống"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 10)
        }
        layout.addView(title)

        permissionButton = Button(this).apply {
            text = "Cấp quyền Truy cập dữ liệu sử dụng"
            setOnClickListener {
                UsageHelper.requestPermission(this@MainActivity)
            }
        }
        layout.addView(permissionButton)

        val refreshButton = Button(this).apply {
            text = "Làm mới danh sách sự kiện"
            setOnClickListener {
                updateLogs()
            }
        }
        layout.addView(refreshButton)

        val clearButton = Button(this).apply {
            text = "Xóa lịch sử Log riêng"
            setOnClickListener {
                LogStore.clear(this@MainActivity)
                updateLogs()
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

        LogStore.add(applicationContext, "System: Tiến trình MainActivity vừa được mở/khôi phục.")
        startTrackerService()
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStatus()
        updateLogs()
    }

    private fun updatePermissionStatus() {
        if (UsageHelper.hasPermission(this)) {
            permissionButton.text = "Đã cấp quyền (Nhấn để xem lại cài đặt)"
            permissionButton.setBackgroundColor(Color.parseColor("#4CAF50"))
        } else {
            permissionButton.text = "Cấp quyền Truy cập dữ liệu sử dụng"
            permissionButton.setBackgroundColor(Color.parseColor("#FF9800"))
        }
    }

    private fun updateLogs() {
        val usageEvents = UsageHelper.getRecentEvents(this)
        val selfLogs = LogStore.getLogs(this)

        val sb = StringBuilder()
        sb.append("--- SỰ KIỆN HỆ THỐNG (USAGE STATS) ---\n")
        for (event in usageEvents) {
            sb.append(event).append("\n")
        }
        sb.append("\n--- LỊCH SỬ KILL TRACKER ---\n")
        for (log in selfLogs) {
            sb.append(log).append("\n")
        }
        logTextView.text = sb.toString()
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
}
