package com.example.killtracker

import android.graphics.Color
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
            text = "Làm mới danh sách"
            setOnClickListener {
                updateLogs()
            }
        }
        layout.addView(refreshButton)

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
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStatus()
        updateLogs()
    }

    private fun updatePermissionStatus() {
        if (UsageHelper.hasPermission(this)) {
            permissionButton.text = "Đã cấp quyền (Nhấn để xem lại cài đặt)"
            permissionButton.setBackgroundColor(Color.parseColor("#4CAF50")) // Xanh lá
        } else {
            permissionButton.text = "Cấp quyền Truy cập dữ liệu sử dụng"
            permissionButton.setBackgroundColor(Color.parseColor("#FF9800")) // Cam cảnh báo
        }
    }

    private fun updateLogs() {
        val events = UsageHelper.getRecentEvents(this)
        val sb = StringBuilder()
        for (event in events) {
            sb.append(event).append("\n\n")
        }
        logTextView.text = sb.toString()
    }
}
