package com.example.order_ndreykitchen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import org.json.JSONException
import org.json.JSONObject

class Register : AppCompatActivity() {
    private lateinit var textinputEt_fullname: com.google.android.material.textfield.TextInputEditText
    private lateinit var textinputEt_email: com.google.android.material.textfield.TextInputEditText
    private lateinit var textinputEt_notelp: com.google.android.material.textfield.TextInputEditText
    private lateinit var textinputEt_password: com.google.android.material.textfield.TextInputEditText
    private lateinit var btn_register: FrameLayout

    private lateinit var fullname: String
    private lateinit var email: String
    private lateinit var notelp: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeItems()

        btn_register.setOnClickListener {
            textinputEt_fullname = findViewById(R.id.textinputEt_fullname)
            textinputEt_notelp = findViewById(R.id.textinputEt_notelp)
            textinputEt_email = findViewById(R.id.textinputEt_email)
            textinputEt_password = findViewById(R.id.textinputEt_password)
            btn_register = findViewById(R.id.btn_register)

            fullname = textinputEt_fullname.text.toString()
            notelp = textinputEt_notelp.text.toString()
            email = textinputEt_email.text.toString()
            password = textinputEt_password.text.toString()

            if (fullname.isEmpty() || notelp.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Stop registration process if any field is empty
            } else {
                registerUser()
            }

        }
    }

    private fun initializeItems () {
        textinputEt_fullname = findViewById(R.id.textinputEt_fullname)
        textinputEt_notelp = findViewById(R.id.textinputEt_notelp)
        textinputEt_email = findViewById(R.id.textinputEt_email)
        textinputEt_password = findViewById(R.id.textinputEt_password)
        btn_register = findViewById(R.id.btn_register)
    }


    private fun registerUser() {
        fullname = textinputEt_fullname.text.toString()
        notelp = textinputEt_notelp.text.toString()
        email = textinputEt_email.text.toString()
        password = textinputEt_password.text.toString()

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/postUser" +
                "?fullname=$fullname" +
                "&email=$email" +
                "&notelp=$notelp" +
                "&password=$password"

        val sr = StringRequest(
            Request.Method.POST,
            urlEndPoints,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.has("error")) {
                        // If there's an error message in the response, display it
                        val errorMessage = jsonResponse.getString("error")
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // If no error message, registration is successful
//                        Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Login::class.java))
                    }
                } catch (e: JSONException) {
                    // If there's a JSON parsing error, display a generic registration failed message
                    Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show()
                }

            },
            { error ->
                Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(applicationContext).add(sr)
    }

}