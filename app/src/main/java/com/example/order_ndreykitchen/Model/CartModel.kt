package com.example.order_ndreykitchen.Model

import android.os.Parcel
import android.os.Parcelable

data class CartModel(
    val id_cart: String? = null,
    val id_user: String? = null,
    val id_menu: String? = null,
    val quantity_cart: Int? = null,
    val nama_menu: String? = null,
    val harga_menu: Int? = null,
    val image_menu: String? = null,
    val kategori_menu: String? = null,
    var quantity: Int = 1,
    var isChecked: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id_cart)
        parcel.writeString(id_user)
        parcel.writeString(id_menu)
        parcel.writeValue(quantity_cart)
        parcel.writeString(nama_menu)
        parcel.writeValue(harga_menu)
        parcel.writeString(image_menu)
        parcel.writeString(kategori_menu)
        parcel.writeInt(quantity)
        parcel.writeByte(if (isChecked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CartModel> {
        override fun createFromParcel(parcel: Parcel): CartModel {
            return CartModel(parcel)
        }

        override fun newArray(size: Int): Array<CartModel?> {
            return arrayOfNulls(size)
        }
    }
}