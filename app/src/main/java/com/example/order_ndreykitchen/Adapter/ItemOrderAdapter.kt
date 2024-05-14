package com.example.order_ndreykitchen.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.order_ndreykitchen.Model.OrderItemModel
import com.example.order_ndreykitchen.R

class ItemOrderAdapter(private val itemList: List<OrderItemModel>) :
    RecyclerView.Adapter<ItemOrderAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.item_order_detail_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvItem: TextView = itemView.findViewById(R.id.tvNamaMenu)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)

        fun bind(item: OrderItemModel) {
            tvItem.text = item.item_orderItem
            tvQuantity.text = "(" + item.quantity_orderItem.toString() + " x)"
        }
    }
}