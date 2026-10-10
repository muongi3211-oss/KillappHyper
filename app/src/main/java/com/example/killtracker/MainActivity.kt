package com.example.killtracker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val textView = TextView(this).apply {
            text = "Kill Tracker đang chạy ngầm...\nHãy mở Logcat hoặc kiểm tra Thanh thông báo!"
            textSize = 18f
            setPadding(40, 40, 40, 40)
        }
        setContentView(textView)

        val serviceIntent = Intent(this, AppKillWatcherService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}
