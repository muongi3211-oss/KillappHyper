package com.kinh.killtracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }

        val btnPermission = Button(this).apply {
            text = "1. Cấp quyền Hiển thị trên ứng dụng khác"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                } else {
                    Toast.makeText(this@MainActivity, "Đã có quyền!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnStart = Button(this).apply {
            text = "2. Bật cửa sổ nổi"
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(this@MainActivity, FloatingService::class.java)
                    startForegroundService(intent)
                    Toast.makeText(this@MainActivity, "Đã bật cửa sổ nổi!", Toast.LENGTH_SHORT).show()
                    finish() // Đóng app chính để vào game
                } else {
                    Toast.makeText(this@MainActivity, "Chưa cấp quyền hiển thị nổi!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        layout.addView(btnPermission)
        layout.addView(btnStart)
        setContentView(layout)
    }
}
