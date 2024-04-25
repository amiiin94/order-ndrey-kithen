package com.example.order_ndreykitchen

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.squareup.picasso.Picasso

class MenuDetail : AppCompatActivity() {
    private lateinit var iv_image_menu_detail: ImageView
    private lateinit var tv_nama_menu_detail: TextView
    private lateinit var tv_harga_menu_detail: TextView
    private lateinit var tv_kategori_menu_detail: TextView
    private lateinit var tv_deskripsi_menu_detail: TextView
    private lateinit var btn_beliLangsung: FrameLayout
    private lateinit var btn_tambahKeranjang: FrameLayout
    private lateinit var btn_back: ImageView

    private lateinit var menuId: String
    private lateinit var nama: String
    private var harga: Int = 0
    private lateinit var image: String
    private lateinit var deskripsi: String
    private lateinit var kategori: String

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
    }

    private fun showDataMenu() {
        // Retrieve data from intent
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
}