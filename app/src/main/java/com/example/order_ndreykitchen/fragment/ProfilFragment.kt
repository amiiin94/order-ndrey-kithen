package com.example.order_ndreykitchen.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.order_ndreykitchen.Cart
import com.example.order_ndreykitchen.EditProfile
import com.example.order_ndreykitchen.CompanionObject.Companion.formatToRupiah
import com.example.order_ndreykitchen.MainActivity
import com.example.order_ndreykitchen.Model.OrderItemModel
import com.example.order_ndreykitchen.Model.OrderModel
import com.example.order_ndreykitchen.OrderDetail
import com.example.order_ndreykitchen.R
import org.json.JSONArray
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfilFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btn_logout: FrameLayout
    private lateinit var picture: TextView
    private lateinit var tvName: TextView
    private lateinit var keranjang: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var editProfil: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var keluar: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var tvLihatSemua: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvTidakAda: TextView
    private lateinit var cvOrder: androidx.cardview.widget.CardView

    private lateinit var status_order: TextView
    private lateinit var date_order: TextView
    private lateinit var orderItem_order: TextView
    private lateinit var quantity_order: TextView
    private lateinit var tvItemSize: TextView
    private lateinit var ivGoToDetail: ImageView
    private lateinit var amount_order: TextView
    private val orderList = mutableListOf<OrderModel>()
    private val orderItemList = mutableListOf<OrderItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeItems()
        getOrderById(requireContext())

        btn_logout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { dialog, which ->
                    logout()
                }
                .setNegativeButton("Tidak") { dialog, which ->
                    // Do nothing
                }
                .show()
        }

        tvLihatSemua.setOnClickListener {
            (requireActivity() as MainActivity).binding.bottomNavigation.selectedItemId = R.id.riwayat
        }

        val fullname = sharedPreferences.getString("fullname_user", "")
        if (!fullname.isNullOrEmpty()) {
            picture.text = fullname[0].toString().uppercase()
        } else {
            picture.visibility = View.GONE
        }

        tvName.text = fullname

        keranjang.setOnClickListener {
            val intent = Intent(requireContext(), Cart::class.java)
            startActivity(intent)
        }

        editProfil.setOnClickListener {
            val intent = Intent(requireContext(), EditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun initializeItems() {
        btn_logout = requireView().findViewById(R.id.btn_logout)
        picture = requireView().findViewById(R.id.picture)
        tvName = requireView().findViewById(R.id.tvName)
        keranjang = requireView().findViewById(R.id.keranjang)
        editProfil = requireView().findViewById(R.id.editProfil)
        keluar = requireView().findViewById(R.id.keluar)
        tvLihatSemua = requireView().findViewById(R.id.tvLihatSemua)
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        status_order = requireView().findViewById(R.id.status_order)
        date_order = requireView().findViewById(R.id.date_order)
        orderItem_order = requireView().findViewById(R.id.orderItem_order)
        quantity_order = requireView().findViewById(R.id.quantity_order)
        tvItemSize = requireView().findViewById(R.id.tvItemSize)
        ivGoToDetail = requireView().findViewById(R.id.ivGoToDetail)
        amount_order = requireView().findViewById(R.id.amount_order)
        tvTidakAda = requireView().findViewById(R.id.tvTidakAda)
        cvOrder = requireView().findViewById(R.id.cvorder)
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun getOrderById(context: Context) {
        val id_user = sharedPreferences.getString("id_user", "")
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getHistoryByUserId?id_user=$id_user"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val order = JSONArray(response)
                    if (order.length() > 0) {
                        orderList.clear()
                        for (i in 0 until order.length()) {
                            val orderJson = order.getJSONObject(i)
                            val id_order = orderJson.getString("_id")
                            val id_user = orderJson.getString("id_user")
                            val amount_order = orderJson.getInt("amount")
                            val date_order = orderJson.getString("date")
                            val payment_order = orderJson.getString("payment")
                            val status_order = orderJson.getString("status")

                            val orders = OrderModel(
                                id_order,
                                id_user,
                                amount_order,
                                date_order,
                                payment_order,
                                status_order
                            )
                            orderList.add(orders)
                        }
                        cvOrder.visibility = View.VISIBLE
                        tvTidakAda.visibility = View.GONE
                    } else {
                        cvOrder.visibility = View.GONE
                        tvTidakAda.visibility = View.VISIBLE
                    }
                    getAllOrderList(requireContext())
                } catch (e: JSONException) {
                    e.printStackTrace()
                    cvOrder.visibility = View.GONE
                    tvTidakAda.visibility = View.VISIBLE
                }
            },
            { error ->
                Toast.makeText(context, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT).show()
                cvOrder.visibility = View.GONE
                tvTidakAda.visibility = View.VISIBLE
            }
        )
        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        requestQueue.add(sr)
    }

    private fun getAllOrderList(context: Context) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getOrderHistoryMenu"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val orderItems = JSONArray(response)
                    if (orderItems.length() > 0) {
                        orderItemList.clear()
                        for (i in 0 until orderItems.length()) {
                            val orderItemJson = orderItems.getJSONObject(i)
                            val id_orderItem = orderItemJson.getString("_id")
                            val id_order = orderItemJson.getString("id_order")
                            val item_orderItem = orderItemJson.getString("item")
                            val quantity_orderItem = orderItemJson.getInt("quantity")

                            val orderItem = OrderItemModel(id_orderItem, id_order, item_orderItem, quantity_orderItem)
                            orderItemList.add(orderItem)
                        }
                    }
                    getLatestOrder()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(context, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT).show()
            }
        )
        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        requestQueue.add(sr)
    }

    private fun getLatestOrder() {
        if (orderList.isNotEmpty()) {
            val order = orderList.last()
            status_order.text = order.status_order
            amount_order.text = formatToRupiah(order.amount_order)
            date_order.text = order.date_order

            val correspondingItem = orderItemList.find { it.id_order == order.id_order }
            orderItem_order?.text = correspondingItem?.item_orderItem ?: "Item not found"

            val firstItemQuantity = orderItemList.firstOrNull { it.id_order == order.id_order }?.quantity_orderItem
            quantity_order.text = "$firstItemQuantity x"

            val itemCount = orderList.count { it.id_order == order.id_order }
            if (itemCount > 1) {
                tvItemSize.text = "+${itemCount - 1} menu lainnya"
            } else {
                tvItemSize.visibility = View.INVISIBLE
            }

            ivGoToDetail.setOnClickListener {
                val detailActivityIntent = Intent(context, OrderDetail::class.java).apply {
                    putExtra("id_order", order.id_order)
                    putExtra("date_order", order.date_order)
                    putExtra("amount_order", order.amount_order)
                    putExtra("status_order", order.status_order)
                    putExtra("payment_order", order.payment_order)
                }
                context?.startActivity(detailActivityIntent)
            }
        } else {
            cvOrder.visibility = View.GONE
            tvTidakAda.visibility = View.VISIBLE
        }
    }
}