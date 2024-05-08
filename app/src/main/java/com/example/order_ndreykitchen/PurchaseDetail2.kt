package com.example.order_ndreykitchen

import android.media.Image
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PurchaseDetail2 : AppCompatActivity() {
    private lateinit var tvIdPesanan: TextView
    private lateinit var tvTotalHarga: TextView
    private lateinit var ivPayment: ImageView
    private lateinit var tvPaymentDetail: TextView
    private lateinit var tvNamaPenjual: TextView
    private lateinit var tvSalinNomor: TextView
    private lateinit var btn_sudah_bayar: FrameLayout
    private var totalHarga: Int = 0
    private var payment: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_purchase_detail2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById()

        totalHarga = intent.getIntExtra("totalHarga", 0)
        payment = intent.getStringExtra("payment") ?: ""

        tvTotalHarga.text = totalHarga.toString()

        when (payment) {
            "dana" -> ivPayment.setImageResource(R.drawable.logo_dana)
            "shopeepay" -> ivPayment.setImageResource(R.drawable.logo_shopeepay)
            "gopay" -> ivPayment.setImageResource(R.drawable.logo_gopay)
            "qris" -> ivPayment.setImageResource(R.drawable.qris_logo)
            // Add more cases if needed for other payment methods
            else -> {
                // Default case if payment method is not recognized
                // You can set a default image or handle the case as needed
            }
        }


    }

    private fun findViewById() {
        tvIdPesanan = findViewById(R.id.tvIdPesanan)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        ivPayment = findViewById(R.id.ivPayment)
        tvPaymentDetail = findViewById(R.id.tvPaymentDetail)
        tvNamaPenjual = findViewById(R.id.tvNamaPenjual)
        tvSalinNomor = findViewById(R.id.tvSalinNomor)
        btn_sudah_bayar = findViewById(R.id.btn_sudah_bayar)
    }


}