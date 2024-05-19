package com.example.order_ndreykitchen.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.order_ndreykitchen.CompanionObject.Companion.formatToRupiah
import com.example.order_ndreykitchen.Model.OrderItemModel
import com.example.order_ndreykitchen.Model.OrderModel
import com.example.order_ndreykitchen.OrderDetail
import com.example.order_ndreykitchen.R
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class OrderAdapter(private var orderList: List<OrderModel>, private val orderItemList: List<OrderItemModel>) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private lateinit var context: Context
    init {
        // Reverse the order list
        orderList = orderList.reversed()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val orderView = inflater.inflate(R.layout.item_order, parent, false)
        return ViewHolder(orderView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]
        holder.status_order.text = order.status_order
        holder.amount_order.text = formatToRupiah(order.amount_order)
        holder.date_order.text = order.date_order

        // Find the corresponding ItemModel based on the order_id
        val correspondingItem = orderItemList.find { it.id_order == order.id_order }

        // Set item name
        holder.orderItem_order?.text = correspondingItem?.item_orderItem ?: "Item not found"

        // Get the quantity of the first item
        val firstItemQuantity = orderItemList.firstOrNull { it.id_order == order.id_order }?.quantity_orderItem

        // Set the quantity
        holder.quantity_order.text = firstItemQuantity.toString() + "x"

        // Get itemList size by id
        val itemCount = orderItemList.count { it.id_order == order.id_order }
        if (itemCount > 1) {
            holder.itemSize.text = "+${itemCount - 1} menu lainnya"
            holder.itemSize.visibility = View.VISIBLE // Ensure it's visible if there are more items
        } else {
            holder.itemSize.visibility = View.INVISIBLE
        }

        holder.cvOrder.setOnClickListener {
            val detailActivityIntent = Intent(context, OrderDetail::class.java).apply {
                putExtra("id_order", order.id_order)
                putExtra("date_order", order.date_order)
                putExtra("amount_order", order.amount_order)
                putExtra("status_order", order.status_order)
                putExtra("payment_order", order.payment_order)
            }

            context.startActivity(detailActivityIntent)
        }
    }



    override fun getItemCount(): Int {
        return orderList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val status_order: TextView = itemView.findViewById(R.id.status_order)
        val date_order: TextView = itemView.findViewById(R.id.date_order)
        val orderItem_order: TextView = itemView.findViewById(R.id.orderItem_order)
        val quantity_order: TextView = itemView.findViewById(R.id.quantity_order)
        val itemSize: TextView = itemView.findViewById(R.id.tvItemSize)
        val amount_order: TextView = itemView.findViewById(R.id.amount_order)
        val cvOrder: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cvorder)


    }

    private fun formatToRupiah(value: Int?): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatRupiah.currency = Currency.getInstance("IDR")

        val formattedValue =
            value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

        return "Rp. $formattedValue"
    }
}