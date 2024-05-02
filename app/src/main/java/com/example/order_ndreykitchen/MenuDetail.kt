package com.example.order_ndreykitchen

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.FrameLayout
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
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class MenuDetail : AppCompatActivity() {
    private lateinit var iv_image_menu_detail: ImageView
    private lateinit var tv_nama_menu_detail: TextView
    private lateinit var tv_harga_menu_detail: TextView
    private lateinit var tv_kategori_menu_detail: TextView
    private lateinit var tv_deskripsi_menu_detail: TextView
    private lateinit var btn_beliLangsung: FrameLayout
    private lateinit var btn_tambahKeranjang: FrameLayout
    private lateinit var btn_back: ImageView

    private lateinit var id_menu: String
    private lateinit var nama: String
    private var harga: Int = 0
    private lateinit var image: String
    private lateinit var deskripsi: String
    private lateinit var kategori: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var id_user: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inizializeItems()
        showDataMenu()

        btn_tambahKeranjang.setOnClickListener {
            postCart(id_user, id_menu)
        }
    }

    private fun inizializeItems() {
        iv_image_menu_detail = findViewById(R.id.iv_image_menu_detail)
        tv_nama_menu_detail = findViewById(R.id.tv_nama_menu_detail)
        tv_kategori_menu_detail = findViewById(R.id.tv_kategori_menu_detail)
        tv_harga_menu_detail = findViewById(R.id.tv_harga_menu_detail)
        tv_deskripsi_menu_detail = findViewById(R.id.tv_deskripsi_menu_detail)
        btn_back = findViewById(R.id.btn_back)
        btn_beliLangsung = findViewById(R.id.btn_beliLangsung)
        btn_tambahKeranjang = findViewById(R.id.btn_tambahKeranjang)

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        id_user = sharedPreferences.getString("id_user", "") ?: ""
    }

    private fun showDataMenu() {
        // Retrieve data from intent
        id_menu = intent.getStringExtra("id_menu") ?: ""
        nama = intent.getStringExtra("nama_menu") ?: ""
        harga = intent.getIntExtra("harga_menu", 0)
        image = intent.getStringExtra("image_menu") ?: ""
        deskripsi = intent.getStringExtra("deskripsi_menu") ?: ""
        kategori = intent.getStringExtra("kategori_menu") ?: ""

        tv_nama_menu_detail.setText(nama)
        tv_kategori_menu_detail.setText(kategori)
        tv_harga_menu_detail.setText(harga.toString())
        tv_deskripsi_menu_detail.setText(deskripsi)
        Picasso.get().load(image).into(iv_image_menu_detail)

    }

    fun postCart(id_user: String, id_menu: String) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/postCart?id_user=$id_user&id_menu=$id_menu"

        val sr = StringRequest(
            Request.Method.POST,
            urlEndPoints,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)

                    // Check if the response contains an error field
                    if (jsonResponse.has("error")) {
                        val errorMessage = jsonResponse.getString("error")
                        // Display toast with the error message
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration successful
                        Toast.makeText(
                            this,
                            "Added Product to Cart!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
                }
            },
            { Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show() }
        )
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(sr)
    }
}