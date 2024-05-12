package com.example.order_ndreykitchen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

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

        tvSimpan.setOnClickListener {
            putUserById()
        }

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

    private fun putUserById() {
        val userId = sharedPreferences.getString("user_id", "")
        val fullname = etFullname.text.toString()
        val notelp = etNoTelp.text.toString()

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/putUserById?_id=$userId&fullname=$fullname&notelp=$notelp"

        val stringRequest = StringRequest(
            Request.Method.PUT,
            urlEndPoints,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)

                    if (jsonResponse.has("error")) {
                        val errorMessage = jsonResponse.getString("error")
                        Toast.makeText(this@EditProfile, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        val editor = sharedPreferences.edit()
                        editor.putString("fullname_user", fullname)
                        editor.putString("notelp_user", notelp)
                        editor.apply()

                        Toast.makeText(this@EditProfile, "Profile has been updated", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@EditProfile, "Failed", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this@EditProfile, "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this@EditProfile).add(stringRequest)
    }
}