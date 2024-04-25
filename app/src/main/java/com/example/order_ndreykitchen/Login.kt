package com.example.order_ndreykitchen

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
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

class Login : AppCompatActivity() {
    private lateinit var textinput_email: com.google.android.material.textfield.TextInputEditText
    private lateinit var textinput_password: com.google.android.material.textfield.TextInputEditText
    private lateinit var btn_login: FrameLayout
    private lateinit var tv_register: TextView

    private lateinit var email: String
    private lateinit var password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeItems()

        btn_login.setOnClickListener {
            login()
        }

        tv_register.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

    }

    private fun initializeItems() {
        textinput_email = findViewById(R.id.textinput_email)
        textinput_password = findViewById(R.id.textinput_password)
        btn_login = findViewById(R.id.btn_login)
        tv_register = findViewById(R.id.tv_register)
    }

    fun login() {
        email = textinput_email.text.toString()
        password = textinput_password.text.toString()


        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getUserByEmailPassword?email=$email&password=$password"

        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val userJson = JSONObject(response)

                    val id_user = userJson.getString("_id")
                    val fullname_user = userJson.getString("fullname")
                    val notelp_user = userJson.getString("notelp")
                    val email_user = userJson.getString("email")
                    val password_user = userJson.getString("password")

                    // Store user data in SharedPreferences
                    val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("id_user", id_user)
                    editor.putString("fullname_user", fullname_user)
                    editor.putString("notelp_user", notelp_user)
                    editor.putString("email_user", email_user)
                    editor.putString("password_user", password_user)
                    editor.apply()

                    Toast.makeText(this@Login, "Login successful!", Toast.LENGTH_SHORT).show()

                    val profileIntent = Intent(this@Login, MainActivity::class.java)
                    startActivity(profileIntent)
                } catch (e: JSONException) {
                    Toast.makeText(this@Login, "Login Unsuccessful!", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this@Login, error.networkResponse?.statusCode.toString(), Toast.LENGTH_SHORT).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }
}