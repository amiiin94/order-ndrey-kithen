package com.example.order_ndreykitchen.Model

data class CartModel (
    val id_cart: String? = null,
    val id_user: String? = null,
    val id_menu: String? = null,
    val quantity_cart: Int? = null,
    val nama_menu: String? = null,
    val harga_menu: Int? = null,
    val image_menu: String? = null,
    val kategori_menu: String? = null,
    var quantity: Int = 0
)