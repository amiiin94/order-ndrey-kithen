package com.example.order_ndreykitchen

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditProfile : AppCompatActivity() {
    private lateinit var btn_back: ImageView
    private lateinit var tvSimpan: TextView
    private lateinit var picture: TextView
    private lateinit var etEmail: com.google.android.material.textfield.TextInputEditText
    private lateinit var etFullname: com.google.android.material.textfield.TextInputEditText
    private lateinit var etNoTelp: com.google.android.material.textfield.TextInputEditText
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initiliazeItems()

        val id_user = sharedPreferences.getString("id_user", "")
        val fullname = sharedPreferences.getString("fullname_user", "")
        val email = sharedPreferences.getString("email_user", "")
        val notelp = sharedPreferences.getString("notelp_user", "")

        if (!fullname.isNullOrEmpty()) {
            picture.text = fullname[0].toString().uppercase()
        } else {
            picture.visibility = View.GONE
        }

        etEmail.setText(email)
        etFullname.setText(fullname)
        etNoTelp.setText(notelp)

    }

    private fun initiliazeItems() {
        btn_back = findViewById(R.id.btn_back)
        tvSimpan = findViewById(R.id.tvSimpan)
        picture = findViewById(R.id.picture)
        etEmail = findViewById(R.id.etEmail)
        etFullname = findViewById(R.id.etFullname)
        etNoTelp = findViewById(R.id.etNoTelp)
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        etEmail.isFocusable = false
        etEmail.isClickable = false

    }
}