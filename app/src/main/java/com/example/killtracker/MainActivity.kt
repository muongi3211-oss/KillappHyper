package com.example.killtracker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tạo giao diện đơn giản trực tiếp bằng code để tránh lỗi thiếu file XML layout
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }

        val title = TextView(this).apply {
            text = "Kill Tracker Monitor"
            textSize = 20f
            setPadding(0, 0, 0, 30)
        }
        layout.addView(title)

        val statusText = TextView(this).apply {
            text = "Trạng thái: Đang giám sát tiến trình ngầm của hệ thống."
            textSize = 14f
        }
        layout.addView(statusText)

        setContentView(layout)

        // Khởi động service an toàn sau khi Activity đã sẵn sàng
        try {
            val intent = Intent(this, AppKillWatcherService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
