package com.kinh.appkiller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
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

        val edtGamePackage = EditText(this).apply {
            hint = "Nhập Package Game (vd: com.vng.pubgmobile)"
            val savedPkg = getSharedPreferences("GameConfig", Context.MODE_PRIVATE)
                .getString("TARGET_GAME", "com.vng.pubgmobile")
            setText(savedPkg)
        }

        val btnSave = Button(this).apply {
            text = "Lưu Package Game"
            setOnClickListener {
                val pkg = edtGamePackage.text.toString().trim()
                getSharedPreferences("GameConfig", Context.MODE_PRIVATE)
                    .edit()
                    .putString("TARGET_GAME", pkg)
                    .apply()
                Toast.makeText(this@MainActivity, "Đã lưu!", Toast.LENGTH_SHORT).show()
            }
        }

        val btnAccessibility = Button(this).apply {
            text = "Bật quyền Hỗ trợ tiếp cận (Accessibility)"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        layout.addView(edtGamePackage)
        layout.addView(btnSave)
        layout.addView(btnAccessibility)
        setContentView(layout)
    }
}
