package com.example.order_ndreykitchen.Model

data class OrderModel(
    val id_order: String? = null,
    val id_user: String? = null,
    val amount_order: Int? = null,
    val date_order: String? = null,
    val payment_order: String? = null,
    val status_order: String? = null
)

