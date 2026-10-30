package com.kinh.appkiller

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent

class GameDetectService : AccessibilityService() {

    private var isTriggered = false

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            // Lấy package game do người dùng lưu trong SharedPreferences
            val prefs = getSharedPreferences("GameConfig", Context.MODE_PRIVATE)
            val targetGame = prefs.getString("TARGET_GAME", "com.vng.pubgmobile") ?: ""

            if (packageName == targetGame) {
                if (!isTriggered) {
                    isTriggered = true
                    
                    // Bật giao diện Thông tin ứng dụng của Battery and Performance
                    openAppInfo("com.miui.powerkeeper")
                    // Bật giao diện Thông tin ứng dụng của Joyose
                    openAppInfo("com.xiaomi.joyose")
                }
            } else if (packageName != "com.miui.powerkeeper" && 
                       packageName != "com.xiaomi.joyose" && 
                       packageName != this.packageName) {
                // Reset trạng thái khi bạn thoát khỏi game sang app khác
                isTriggered = false
            }
        }
    }

    private fun openAppInfo(targetPkg: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$targetPkg")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInterrupt() {}
}
