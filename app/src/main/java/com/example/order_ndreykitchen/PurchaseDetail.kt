package com.example.order_ndreykitchen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.order_ndreykitchen.Adapter.PurchaseDetailAdapter
import com.example.order_ndreykitchen.Model.CartModel
import com.example.order_ndreykitchen.Model.OrderModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

class PurchaseDetail : AppCompatActivity() {

    private lateinit var btn_bayar_sekarang: FrameLayout
    private var totalHarga = 0 // Declare totalHarga property here
    private lateinit var selectedItems: ArrayList<CartModel> // Declare selectedItems property here
    private lateinit var tvTotalHarga: TextView
    private lateinit var rv: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private var payment: String = "dana"

    private lateinit var purchase_dana: FrameLayout
    private lateinit var purchase_shopeepay: FrameLayout
    private lateinit var purchase_gopay: FrameLayout
    private lateinit var purchase_qris: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_purchase_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val id_user = sharedPreferences.getString("id_user", "") ?: ""


        id_user?.let { Log.d("id_user", it) }

        selectedItems = (intent.getSerializableExtra("selectedItems") as ArrayList<CartModel>?)!!
        totalHarga = intent.getIntExtra("totalHarga", 0)

        rv = findViewById(R.id.rvPurchaseDetail)

        btn_bayar_sekarang = findViewById(R.id.btn_bayar_sekarang)
        btn_bayar_sekarang.setOnClickListener {
            val intent = Intent(this, PurchaseDetail2::class.java)
            intent.putExtra("totalHarga", totalHarga) // Pass totalHarga as extra
            intent.putExtra("payment", payment) // Pass payment as extra
            intent.putExtra("selectedItems", ArrayList(selectedItems))
            startActivity(intent)
            postIdOrder()
        }


        // Check if selectedItems is not null
        Log.d("selected Items", selectedItems.toString())
        calculateTotalHarga()

        formatToRupiah(totalHarga)

        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        tvTotalHarga.text = totalHarga.toString()

        displayRecycleview()
        purchaseSelector()

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

    private fun displayRecycleview() {

        rv.layoutManager = GridLayoutManager(this, 1)

        val horizontalSpace =resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        val verticalSpace = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        rv.addItemDecoration(SpaceItemDecoration(horizontalSpace, verticalSpace))

        val cartAdapter = PurchaseDetailAdapter(selectedItems)
        rv.adapter = cartAdapter
    }

    private fun purchaseSelector() {
        purchase_dana = findViewById(R.id.purchase_dana)
        purchase_shopeepay = findViewById(R.id.purchase_shopeepay)
        purchase_gopay = findViewById(R.id.purchase_gopay)
        purchase_qris = findViewById(R.id.purchase_qris)

        val defaultSelected = purchase_dana // Set default selected item

        purchase_dana.setOnClickListener {
            if (!purchase_dana.isSelected) {
                resetSelection()
                selectPurchaseMethod(purchase_dana)
                payment = "dana" // Set payment for purchase_dana
            }
        }

        purchase_shopeepay.setOnClickListener {
            if (!purchase_shopeepay.isSelected) {
                resetSelection()
                selectPurchaseMethod(purchase_shopeepay)
                payment = "shopeepay" // Set payment for purchase_shopeepay
            }
        }

        purchase_gopay.setOnClickListener {
            if (!purchase_gopay.isSelected) {
                resetSelection()
                selectPurchaseMethod(purchase_gopay)
                payment = "gopay" // Set payment for purchase_gopay
            }
        }

        purchase_qris.setOnClickListener {
            if (!purchase_qris.isSelected) {
                resetSelection()
                selectPurchaseMethod(purchase_qris)
                payment = "qris" // Set payment for purchase_qris
            }
        }


        // Set default selection
        selectPurchaseMethod(defaultSelected)
    }

    private fun selectPurchaseMethod(selectedItem: FrameLayout) {
        selectedItem.isSelected = true // Set selected state to true
        // Set background drawable based on the selected state
        if (selectedItem.isSelected) {
            selectedItem.background = ContextCompat.getDrawable(this, R.drawable.purchase_bg_selected)
        } else {
            selectedItem.background = ContextCompat.getDrawable(this, R.drawable.purchase_bg)
        }
    }

    private fun resetSelection() {
        // Reset background drawable for all items
        purchase_dana.isSelected = false
        purchase_shopeepay.isSelected = false
        purchase_gopay.isSelected = false
        purchase_qris.isSelected = false

        // Set default background drawable for all items
        purchase_dana.background = ContextCompat.getDrawable(this, R.drawable.purchase_bg)
        purchase_shopeepay.background = ContextCompat.getDrawable(this, R.drawable.purchase_bg)
        purchase_gopay.background = ContextCompat.getDrawable(this, R.drawable.purchase_bg)
        purchase_qris.background = ContextCompat.getDrawable(this, R.drawable.purchase_bg)
    }

    fun postIdOrder() {
        val id_user = sharedPreferences.getString("id_user", "") ?: ""


        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date())

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/postIdOrder?id_user=$id_user&totalHarga=$totalHarga&date=$currentDate&payment=$payment"

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
                        Toast.makeText(this@PurchaseDetail, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration successful
                        Toast.makeText(
                            this@PurchaseDetail,
                            "record id have been added",
                            Toast.LENGTH_SHORT
                        ).show()
                        postItemsWithQuantity()
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this@PurchaseDetail, "failed", Toast.LENGTH_SHORT).show()
                }
            }
        ) { Toast.makeText(this@PurchaseDetail, "Registration failed", Toast.LENGTH_SHORT).show() }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    fun postItemOrder(item: String?, quantity: Int) {
        val id_user = sharedPreferences.getString("id_user", "") ?: ""

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/postItemOrder?id_user=$id_user&item=$item&quantity=$quantity"

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
                        Toast.makeText(this@PurchaseDetail, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Post successful
                        Toast.makeText(this@PurchaseDetail, "Item posted successfully", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(this@PurchaseDetail, "Failed to post item", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Toast.makeText(this@PurchaseDetail, "Failed to post item", Toast.LENGTH_SHORT).show()
        }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    fun postItemsWithQuantity() {
        for (menu in selectedItems) {
            if (menu.quantity > 0) {
                postItemOrder(menu.nama_menu, menu.quantity)
            }
        }
    }






}
