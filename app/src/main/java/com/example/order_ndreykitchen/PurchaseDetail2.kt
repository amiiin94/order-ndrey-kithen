package com.example.order_ndreykitchen

import android.content.Intent
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
import com.example.order_ndreykitchen.Model.CartModel
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

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
    private lateinit var selectedItems: ArrayList<CartModel> // Declare selectedItems property here

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
        selectedItems = (intent.getSerializableExtra("selectedItems") as ArrayList<CartModel>?)!!

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

        btn_sudah_bayar.setOnClickListener {
            postIdRecord()

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

    fun postIdRecord() {

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/postIdPemasukan"

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
                        Toast.makeText(this@PurchaseDetail2, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration successful
                        Toast.makeText(
                            this@PurchaseDetail2,
                            "record id have been added",
                            Toast.LENGTH_SHORT
                        ).show()
                        putAmount(totalHarga)
                        postItemsWithQuantity()
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this@PurchaseDetail2, "failed", Toast.LENGTH_SHORT).show()
                }
            }
        ) { Toast.makeText(this@PurchaseDetail2, "Registration failed", Toast.LENGTH_SHORT).show() }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    fun putAmount(totalHarga: Int) {
        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date())

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/putTotalHargaOnLastRecord?amount=$totalHarga&date=$currentDate"

        val sr = StringRequest(
            Request.Method.PUT,
            urlEndPoints,
            { response ->
                if (response == "\"Total harga updated successfully for the last document.\"") {
                    // Update successful
                    Toast.makeText(this@PurchaseDetail2, "Amount Updated", Toast.LENGTH_SHORT).show()

                    // Redirect to the main activity
                    val mainActivityIntent = Intent(this@PurchaseDetail2, MainActivity::class.java)
                    startActivity(mainActivityIntent)
                } else {
                    // Display toast with the response message
                    Toast.makeText(this@PurchaseDetail2, response, Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Handle Volley error
                error.printStackTrace()
                Toast.makeText(this@PurchaseDetail2, "Failed: " + error.message, Toast.LENGTH_SHORT).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    fun postItemMenu(item: String?, quantity: Int) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/postItemMenuPemasukan?item=$item&quantity=$quantity"

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
                        Toast.makeText(this@PurchaseDetail2, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Post successful
                        Toast.makeText(this@PurchaseDetail2, "Item posted successfully", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this@PurchaseDetail2, "Failed to post item", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Toast.makeText(this@PurchaseDetail2, "Failed to post item", Toast.LENGTH_SHORT).show()
        }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    fun postItemsWithQuantity() {
        for (menu in selectedItems) {
            if (menu.quantity > 0) {
                postItemMenu(menu.nama_menu, menu.quantity)
            }
        }
    }


}