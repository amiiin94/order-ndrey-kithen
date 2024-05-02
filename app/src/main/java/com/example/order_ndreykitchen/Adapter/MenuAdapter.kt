package com.example.order_ndreykitchen.Adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.order_ndreykitchen.MenuDetail
import com.example.order_ndreykitchen.Model.MenuModel
import com.example.order_ndreykitchen.R
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class MenuAdapter(private val menuList: MutableList<MenuModel>) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var id_user: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        id_user = sharedPreferences.getString("id_user", "") ?: ""

        val inflater = LayoutInflater.from(context)
        val menuView = inflater.inflate(R.layout.item_menu, parent, false)
        return ViewHolder(menuView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menuList[position]
        holder.nama_menu.text = menu.nama_menu
        holder.harga_menu.text = formatToRupiah(menu.harga_menu)
        Picasso.get().load(menu.image_menu).into(holder.image_menu)

        holder.menu_detail.setOnClickListener {
            val intent = Intent(context, MenuDetail::class.java).apply {
                // Pass the menu ID or any other necessary data to EditMenuActivity
                putExtra("id_menu", menu.id_menu)
                putExtra("nama_menu", menu.nama_menu)
                putExtra("harga_menu", menu.harga_menu)
                putExtra("kategori_menu", menu.kategori_menu)
                putExtra("deskripsi_menu", menu.deskripsi_menu)
                putExtra("image_menu", menu.image_menu)
            }
            context.startActivity(intent)
        }

        holder.btn_addToCart.setOnClickListener {
            postCart(id_user, menu.id_menu.toString())
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image_menu: ImageView = itemView.findViewById(R.id.iv_image_menu)
        val nama_menu: TextView = itemView.findViewById(R.id.tv_nama_menu)
        val harga_menu: TextView = itemView.findViewById(R.id.tv_harga_menu)
        val menu_detail: com.google.android.material.card.MaterialCardView = itemView.findViewById(R.id.card_menu)
        val btn_addToCart: com.google.android.material.button.MaterialButton = itemView.findViewById(R.id.btn_tambah)

    }

    private fun formatToRupiah(value: Int?): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

        return "Rp. $formattedValue"
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
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // Registration successful
                        Toast.makeText(
                            context,
                            "Added Product to Cart!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing error
                    e.printStackTrace()
                    Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
                }
            },
            { Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show() }
        )
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(sr)
    }

}