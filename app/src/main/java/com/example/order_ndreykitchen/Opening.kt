package com.example.order_ndreykitchen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Opening : AppCompatActivity() {

    private lateinit var next_btn: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the opening activity has been shown before
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("isFirstRun", true)) {
            // If it's the first run, show the opening activity
            showOpeningActivity()
        } else {
            // If not the first run, proceed to main activity
            startMainActivity()
        }
    }

    private fun showOpeningActivity() {
        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Set content view
        setContentView(R.layout.activity_opening)

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize next button
        next_btn = findViewById(R.id.next_btn)
        next_btn.setOnClickListener {
            // Start main activity
            startMainActivity()

            // Set isFirstRun flag to false
            sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this@Opening, MainActivity::class.java)
        startActivity(intent)
        finish() // Finish the current activity so that it's not accessible via back button
    }
}