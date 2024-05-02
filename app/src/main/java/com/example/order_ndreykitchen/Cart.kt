package com.example.order_ndreykitchen

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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

class Cart : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var id_user: String
    private val cartList = mutableListOf<CartModel>()
    private lateinit var rvCart: RecyclerView
    var isFirstClick = true
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        id_user = sharedPreferences.getString("id_user", "") ?: ""

        rvCart = findViewById(R.id.rvCart)


        getOrderById()

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

        val cartAdapter = CartAdapter(cartList, this)
        rvCart.adapter = cartAdapter
    }


}
