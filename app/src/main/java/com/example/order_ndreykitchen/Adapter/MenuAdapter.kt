package com.example.order_ndreykitchen.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.order_ndreykitchen.MenuDetail
import com.example.order_ndreykitchen.Model.MenuModel
import com.example.order_ndreykitchen.R
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class MenuAdapter(private val menuList: MutableList<MenuModel>) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
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
                putExtra("menu_id", menu.id_menu)
                putExtra("nama_menu", menu.nama_menu)
                putExtra("harga_menu", menu.harga_menu)
                putExtra("kategori_menu", menu.kategori_menu)
                putExtra("deskripsi_menu", menu.deskripsi_menu)
                putExtra("image_menu", menu.image_menu)
            }
            context.startActivity(intent)
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

    }

    private fun formatToRupiah(value: Int?): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

        return "Rp. $formattedValue"
    }
}