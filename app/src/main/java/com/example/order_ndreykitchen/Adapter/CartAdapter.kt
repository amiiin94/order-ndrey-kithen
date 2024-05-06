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
import com.example.order_ndreykitchen.R
import com.squareup.picasso.Picasso

class CartAdapter(private val cartItems: List<CartModel>,
                  private val quantityChangeListener: QuantityChangeListener,
                  private val activity: Cart) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cartItems[position]

        holder.kategoriCart.text = cartItem.kategori_menu
        holder.namaCart.text = cartItem.nama_menu
        holder.hargaCart.text = cartItem.harga_menu.toString()
        holder.quantityTextView.text = cartItem.quantity_cart.toString()
        Picasso.get().load(cartItem.image_menu).into(holder.imageCart)
//        holder.checkbox.isChecked = cartItem.isChecked

        holder.imageViewDelete.setOnClickListener {
            // Implement your delete logic here
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
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)

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

    interface QuantityChangeListener {
        fun onQuantityChanged()
    }
}