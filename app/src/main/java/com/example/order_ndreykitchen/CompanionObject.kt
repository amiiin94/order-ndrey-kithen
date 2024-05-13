package com.example.order_ndreykitchen

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class CompanionObject {
    companion object {
        fun formatToRupiah(value: Int?): String {
            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            formatRupiah.currency = Currency.getInstance("IDR")

            val formattedValue =
                value?.let { formatRupiah.format(it.toLong()).replace("Rp", "").trim() }

            return "Rp. $formattedValue"
        }
    }
}
