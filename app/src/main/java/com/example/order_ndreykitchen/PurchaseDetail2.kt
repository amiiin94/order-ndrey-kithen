package com.example.order_ndreykitchen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.order_ndreykitchen.Model.CartModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale
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
    private lateinit var sharedPreferences: SharedPreferences
    private var id_order: String = ""
    private lateinit var qris: LinearLayout
    private lateinit var qrisgone: LinearLayout
    private lateinit var qriscode: ImageView
    private lateinit var unduhqris: TextView
    private lateinit var tvTimer: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val totalTime = 10 * 60 * 1000L // 10 minutes in milliseconds
    private lateinit var status: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setContentView(R.layout.activity_purchase_detail2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        findViewById()

        totalHarga = intent.getIntExtra("totalHarga", 0)
        payment = intent.getStringExtra("payment") ?: ""
        selectedItems = (intent.getSerializableExtra("selectedItems") as ArrayList<CartModel>?)!!

        tvTotalHarga.text = formatToRupiah(totalHarga)

        when (payment) {
            "dana" -> ivPayment.setImageResource(R.drawable.logo_dana)
            "shopeepay" -> ivPayment.setImageResource(R.drawable.logo_shopeepay)
            "gopay" -> ivPayment.setImageResource(R.drawable.logo_gopay)
            "qris" -> {
                // Show qris layout and hide qrisgone layout
                qris.visibility = View.VISIBLE
                qrisgone.visibility = View.GONE
            }
            // Add more cases if needed for other payment methods
            else -> {
                // Default case if payment method is not recognized
                // You can set a default image or handle the case as needed
            }
        }

        btn_sudah_bayar.setOnClickListener {
            postIdRecord()
            putStatusOrder("Pesanan Diproses")
            deleteCartByUserId()
            // Redirect to the main activity
            val mainActivityIntent = Intent(this@PurchaseDetail2, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }

        tvSalinNomor.setOnClickListener {
            // Call function to copy text to clipboard
            copyTextToClipboard(tvPaymentDetail.text.toString())
        }

        unduhqris.setOnClickListener {
            // Call function to download the QR code
            downloadQRCode()
        }

        getLatestIdRecord()
        startTimer()
    }

    private fun findViewById() {
        tvIdPesanan = findViewById(R.id.tvIdPesanan)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        ivPayment = findViewById(R.id.ivPayment)
        tvPaymentDetail = findViewById(R.id.tvPaymentDetail)
        tvNamaPenjual = findViewById(R.id.tvNamaPenjual)
        tvSalinNomor = findViewById(R.id.tvSalinNomor)
        btn_sudah_bayar = findViewById(R.id.btn_sudah_bayar)
        qris = findViewById(R.id.qris)
        qrisgone = findViewById(R.id.qrisgone)
        qriscode = findViewById(R.id.qriscode)
        unduhqris = findViewById(R.id.unduhqris)
        tvTimer = findViewById(R.id.tv_timer)
        status = findViewById(R.id.status)

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

    private fun getLatestIdRecord() {
        val id_user = sharedPreferences.getString("id_user", "") ?: ""
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getLatestidOrderByIdUser?id_user=$id_user"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    // Parse the JSON response as an array
                    val jsonArray = JSONArray(response)
                    // Check if the array is not empty
                    if (jsonArray.length() > 0) {
                        // Get the first object from the array
                        val jsonObject = jsonArray.getJSONObject(0)
                        // Extract the "_id" field from the object
                        id_order = jsonObject.getString("_id")
                        // Set the id_order text view
                        tvIdPesanan.text = id_order
                    } else {
                        // Handle case when the array is empty
                        Toast.makeText(this@PurchaseDetail2, "Empty response", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this@PurchaseDetail2, error.toString().trim(), Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    private fun putStatusOrder(status: String) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/putStatusOrder?_id=$id_order&status=$status"

        val sr = StringRequest(
            Request.Method.PUT,
            urlEndPoints,
            { response ->
                Toast.makeText(this@PurchaseDetail2, response, Toast.LENGTH_SHORT).show()
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this@PurchaseDetail2, "Failed: " + error.message, Toast.LENGTH_SHORT).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    private fun formatToRupiah(value: Int?): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

        // Remove the ,00 at the end
        val cleanedValue = formattedValue?.replace(",00", "")

        return "Rp. $cleanedValue"
    }

    private fun copyTextToClipboard(text: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun deleteCartByUserId() {
        val id_user = sharedPreferences.getString("id_user", "") ?: ""
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/deleteCartByUserId?id_user=$id_user"
        val sr = StringRequest(
            Request.Method.DELETE,
            urlEndPoints,
            { response ->
                Toast.makeText(this@PurchaseDetail2, "Cart Deleted", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this@PurchaseDetail2, "Error deleting menu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(sr)
    }

    private fun downloadQRCode() {
        // Get the QR code image from the ImageView
        val qrCodeBitmap = qriscode.drawable.toBitmap()

        // Define the directory and file name for saving the image
        val directory = File(getExternalFilesDir(null), "QR Codes")
        directory.mkdirs() // Create directory if it doesn't exist
        val fileName = "qris_code.png"
        val file = File(directory, fileName)

        // Save the QR code image to external storage
        try {
            FileOutputStream(file).use { out ->
                qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            // Notify the user that the download is successful
            Toast.makeText(this@PurchaseDetail2, "QR Code saved to Photos", Toast.LENGTH_SHORT).show()

            // Refresh the gallery so that the image appears in the Photos app
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = Uri.fromFile(file)
            sendBroadcast(mediaScanIntent)
        } catch (e: IOException) {
            e.printStackTrace()
            // Notify the user if there's an error in saving the QR code
            Toast.makeText(this@PurchaseDetail2, "Failed to save QR Code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000 % 60).toString().padStart(2, '0')
                tvTimer.text = "$minutes:$seconds"
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                btn_sudah_bayar.isEnabled = false
                btn_sudah_bayar.setBackgroundColor(getColor(R.color.abu))
                status.text = "Pesanan Kadaluarsa"
                putStatusOrder("Pesanan Dibatalkan")
            }
        }
        countDownTimer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}