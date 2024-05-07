package com.example.order_ndreykitchen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Choreographer.FrameData
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.order_ndreykitchen.Model.CartModel
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class PurchaseDetail : AppCompatActivity() {

    private lateinit var btn_bayar_sekarang: FrameLayout
    private var totalHarga = 0 // Declare totalHarga property here
    private lateinit var selectedItems: ArrayList<CartModel> // Declare selectedItems property here
    private lateinit var tvTotalHarga: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_purchase_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btn_bayar_sekarang = findViewById(R.id.btn_bayar_sekarang)
        btn_bayar_sekarang.setOnClickListener {
            val intent = Intent(this, PurchaseDetail2::class.java)
            startActivity(intent)
        }

        selectedItems = (intent.getSerializableExtra("selectedItems") as ArrayList<CartModel>?)!!
        totalHarga = intent.getIntExtra("totalHarga", 0)

        // Check if selectedItems is not null
        Log.d("selected Items", selectedItems.toString())
        calculateTotalHarga()

        formatToRupiah(totalHarga)

        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        tvTotalHarga.text = totalHarga.toString()

    }

    private fun calculateTotalHarga() {
        totalHarga = 0 // Reset totalHarga before calculating
        for (cart in selectedItems) {
            // Check if the checkbox is checked for the item
            if (cart.isChecked) {
                totalHarga += cart.harga_menu?.times(cart.quantity) ?: 0
            }
        }
        val totalHargaTextView: TextView = findViewById(R.id.tvTotalHarga)
        totalHargaTextView.text = "Rp$totalHarga"
    }

    private fun formatToRupiah(value: Int?): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

        return "Rp. $formattedValue"
    }
}
