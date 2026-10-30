package com.kinh.gmsprotect

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPermission = findViewById<Button>(R.id.btnPermission)
        val btnStartService = findViewById<Button>(R.id.btnStartService)

        btnPermission.setOnClickListener {
            if (!hasUsageStatsPermission()) {
                // Mở cài đặt quyền truy cập sử dụng
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            } else {
                Toast.makeText(this, "Đã được cấp quyền truy cập!", Toast.LENGTH_SHORT).show()
            }
        }

        btnStartService.setOnClickListener {
            if (hasUsageStatsPermission()) {
                val serviceIntent = Intent(this, AppCheckService::class.java)
                startForegroundService(serviceIntent)
                Toast.makeText(this, "Đã khởi động dịch vụ bảo vệ nền!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Vui lòng cấp quyền Truy cập sử dụng trước!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}
