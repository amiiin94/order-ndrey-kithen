package com.example.order_ndreykitchen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.order_ndreykitchen.Adapter.CartAdapter
import com.example.order_ndreykitchen.Adapter.MenuAdapter
import com.example.order_ndreykitchen.Model.CartModel
import com.example.order_ndreykitchen.Model.MenuModel
import org.json.JSONArray
import org.json.JSONException
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class Cart : AppCompatActivity(), CartAdapter.QuantityChangeListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var id_user: String
    private val cartList = mutableListOf<CartModel>()
    private lateinit var rvCart: RecyclerView
    private var totalHarga = 0
    private lateinit var selectAll: CheckBox
    private lateinit var btn_bayar: FrameLayout
    private lateinit var btn_back: ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        id_user = sharedPreferences.getString("id_user", "") ?: ""

        id_user?.let { Log.d("id_user", it) }

        rvCart = findViewById(R.id.rvCart)

        selectAll = findViewById(R.id.selectAll)
        selectAll.setOnCheckedChangeListener { _, isChecked ->
            selectAllItems(isChecked)
        }

        btn_bayar = findViewById(R.id.btn_bayar)
        btn_bayar.setOnClickListener {
            // Filter the cartList to get selected items
            val selectedItems = cartList.filter { it.isChecked }

            // Calculate totalHarga
            val totalHarga = calculateTotalHarga()

            // Check if any item is selected
            if (selectedItems.isNotEmpty()) {
                // Create an Intent to start the next activity
                val intent = Intent(this, PurchaseDetail::class.java)

                // Convert the selectedItems list to serializable format and pass it to the next activity
                intent.putExtra("selectedItems", ArrayList(selectedItems))

                // Start the next activity
                startActivity(intent)
            } else {
                // Show a toast message if no item is selected
                Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show()
            }
        }

        btn_back = findViewById(R.id.btn_back)
        btn_back.setOnClickListener {
            btn_back.setOnClickListener {
                val mainActivityIntent = Intent(this@Cart, MainActivity::class.java)
                mainActivityIntent.putExtra("selected_tab", R.id.menu)
                startActivity(mainActivityIntent)
            }
        }

        getOrderById()
        calculateTotalHarga()

        }

    private fun getOrderById() {

        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getCartByIdUser?id_user=$id_user"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val carts = JSONArray(response)
                    if (carts.length() > 0) {
                        cartList.clear()
                        for (i in 0 until carts.length()) {
                            val cartJson = carts.getJSONObject(i)

                            val id_cart = cartJson.getString("_id")
                            val id_user = cartJson.getString("id_user")
                            val id_menu = cartJson.getString("id_menu")
                            val quantity_cart = cartJson.getInt("quantity")
                            val nama_menu = cartJson.getString("nama")
                            val harga_menu = cartJson.getInt("harga")
                            val image_menu = cartJson.getString("image")
                            val kategori_menu = cartJson.getString("kategori")

                            val cart = CartModel(id_cart, id_user, id_menu, quantity_cart, nama_menu, harga_menu, image_menu, kategori_menu)
                            cartList.add(cart)

                        }
                        displayRecycleview()
                        Log.d("Cart", "cartList: $cartList")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(this.applicationContext)
        requestQueue.add(sr)
    }

    private fun displayRecycleview() {
        rvCart.layoutManager = GridLayoutManager(this, 1)

        val horizontalSpace =resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        val verticalSpace = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        rvCart.addItemDecoration(SpaceItemDecoration(horizontalSpace, verticalSpace))

        val cartAdapter = CartAdapter(cartList, this, this, this)
        rvCart.adapter = cartAdapter
    }

    override fun onQuantityChanged() {
        calculateTotalHarga()
    }

    private fun calculateTotalHarga() {
        totalHarga = 0 // Reset totalHarga before calculating
        for (cart in cartList) {
            // Check if the checkbox is checked for the item
            if (cart.isChecked) {
                totalHarga += cart.harga_menu?.times(cart.quantity) ?: 0
            }
        }
        val totalHargaTextView: TextView = findViewById(R.id.tvTotalHarga)
        totalHargaTextView.text = formatToRupiah(totalHarga)
    }

    private fun selectAllItems(isChecked: Boolean) {
        for (cart in cartList) {
            cart.isChecked = isChecked
        }
        // Notify the adapter of the change
        rvCart.adapter?.notifyDataSetChanged()
        // Calculate total price after selecting/deselecting all items
        calculateTotalHarga()
    }

    private fun formatToRupiah(value: Int?): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

        // Remove the ,00 at the end
        val cleanedValue = formattedValue?.replace(",00", "")

        return "Rp. $cleanedValue"
    }





}
