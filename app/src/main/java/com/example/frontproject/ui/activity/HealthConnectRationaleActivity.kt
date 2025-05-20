package com.example.frontproject.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class HealthConnectRationaleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Вы можете получить список разрешений, для которых запрашивается объяснение:
        // val permissions = intent.getStringArrayListExtra("androidx.health.connect.extra.PERMISSIONS_RATIONALE")
        // и использовать их для более детального сообщения.

        AlertDialog.Builder(this)
            .setTitle("Необходимы разрешения")
            .setMessage("Для отслеживания вашей активности и отображения количества шагов, приложению нужен доступ к данным Health Connect. Пожалуйста, предоставьте разрешения на следующем экране.")
            .setPositiveButton("Понятно") { _, _ ->
                setResult(Activity.RESULT_OK)
                finish()
            }
            .setOnCancelListener {
                // Если пользователь отменяет диалог (например, кнопкой "Назад")
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            .setCancelable(false) // Опционально: чтобы пользователь обязательно нажал кнопку
            .show()
    }
}