package com.example.order_ndreykitchen.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.order_ndreykitchen.Cart
import com.example.order_ndreykitchen.Model.CartModel
import com.example.order_ndreykitchen.PurchaseDetail
import com.example.order_ndreykitchen.R
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class PurchaseDetailAdapter(private val selectedItems: List<CartModel>) : RecyclerView.Adapter<PurchaseDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseDetailAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_purchase_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = selectedItems[position]

        holder.namaCart.text = cartItem.nama_menu
        holder.hargaCart.text = formatToRupiah(cartItem.harga_menu?.times(cartItem.quantity))
        holder.quantityTextView.text = "(" + cartItem.quantity.toString() + "x)"
    }

    override fun getItemCount(): Int {
        return selectedItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaCart: TextView = itemView.findViewById(R.id.tv_nama_menu)
        val hargaCart: TextView = itemView.findViewById(R.id.tv_harga_menu)
        val quantityTextView: TextView = itemView.findViewById(R.id.tv_quantity_menu)
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