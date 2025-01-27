package com.example.order_ndreykitchen.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.order_ndreykitchen.Cart
import com.example.order_ndreykitchen.Model.CartModel
import com.example.order_ndreykitchen.R
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class CartAdapter(private val cartItems: MutableList<CartModel>,
                  private val quantityChangeListener: QuantityChangeListener,
                  private val activity: Cart,
                  private val context: Context, // Add context as a parameter
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cartItems[position]

        holder.kategoriCart.text = cartItem.kategori_menu
        holder.namaCart.text = cartItem.nama_menu
        holder.hargaCart.text = formatToRupiah(cartItem.harga_menu)
        holder.quantityTextView.text = cartItem.quantity.toString()
        if(cartItem.image_menu != "") {
            Picasso.get().load(cartItem.image_menu).into(holder.imageCart)
        } else {
            holder.imageCart.setImageResource(R.drawable.noimage) // Or any default image or null

        }


        holder.imageViewDelete.setOnClickListener {
            val cartItem = cartItems[position]
            deleteCartById(cartItem.id_cart.toString(), position)
        }

    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        val imageCart: ImageView = itemView.findViewById(R.id.image_cart)
        val imageViewDelete: ImageView = itemView.findViewById(R.id.imageView24)
        val minus_btn: ImageView = itemView.findViewById(R.id.minus_btn)
        val plus_btn: ImageView = itemView.findViewById(R.id.plus_btn)
        val kategoriCart: TextView = itemView.findViewById(R.id.kategori_cart)
        val namaCart: TextView = itemView.findViewById(R.id.nama_cart)
        val hargaCart: TextView = itemView.findViewById(R.id.harga_cart)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantity)

        init {
            // Plus button click listener
            plus_btn.setOnClickListener {
                var quantity = quantityTextView.text.toString().toInt()
                quantity++
                quantityTextView.text = quantity.toString()
                cartItems[adapterPosition].quantity = quantity // Update quantity in the corresponding MenuModel
                quantityChangeListener.onQuantityChanged() // Notify activity of quantity change
            }

            // Minus button click listener
            minus_btn.setOnClickListener {
                var quantity = quantityTextView.text.toString().toInt()
                if (quantity > 0) {
                    quantity--
                    quantityTextView.text = quantity.toString()
                    cartItems[adapterPosition].quantity = quantity // Update quantity in the corresponding MenuModel
                    quantityChangeListener.onQuantityChanged() // Notify activity of quantity change
                }
            }

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val cartItem = cartItems[position]
                    cartItem.isChecked = isChecked
                    quantityChangeListener.onQuantityChanged() // Notify activity of checkbox state change
                }
            }

            // Set an OnClickListener for individual checkboxes
            checkbox.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val cartItem = cartItems[position]
                    cartItem.isChecked = checkbox.isChecked
                    quantityChangeListener.onQuantityChanged()
                }
            }

        }
    }

    private fun deleteCartById(cartId: String, position: Int) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/deleteCartByCartId?_id=$cartId"
        val sr = StringRequest(
            Request.Method.DELETE,
            urlEndPoints,
            { response ->
//                Toast.makeText(context, "Menu deleted successfully", Toast.LENGTH_SHORT).show()
                removeItem(position)
            },
            { error ->
                Toast.makeText(context, "Error deleting menu: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        requestQueue.add(sr)
    }

    private fun removeItem(position: Int) {
        cartItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    private fun formatToRupiah(value: Int?): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue = value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

        // Remove the ,00 at the end
        val cleanedValue = formattedValue?.replace(",00", "")

        return "Rp. $cleanedValue"
    }


    interface QuantityChangeListener {
        fun onQuantityChanged()
    }
}