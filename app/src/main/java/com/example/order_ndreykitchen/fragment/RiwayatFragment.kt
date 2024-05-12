package com.example.order_ndreykitchen.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.order_ndreykitchen.Adapter.OrderAdapter
import com.example.order_ndreykitchen.Model.OrderItemModel
import com.example.order_ndreykitchen.Model.OrderModel
import com.example.order_ndreykitchen.R
import com.example.order_ndreykitchen.SpaceItemDecoration
import org.json.JSONArray
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RiwayatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RiwayatFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val orderList = mutableListOf<OrderModel>()
    private val orderItemList = mutableListOf<OrderItemModel>()
    private lateinit var rvOrder: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)

        rvOrder = view.findViewById(R.id.rvOrder)

        getOrderById(requireContext())


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
                        getOrderHistoryMenu(requireContext())
                        Log.d("RecordFragment", "recordList: $orderList")
                    }
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

    private fun getOrderHistoryMenu(context: Context) {
        val urlEndPoints =
            "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getOrderHistoryMenu"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    val orderItems = JSONArray(response)
                    if (orderItems.length() > 0) {
                        orderItemList.clear() // Clearing the list here
                        for (i in 0 until orderItems.length()) {
                            // Adding items to itemMenuList here
                            val orderItemJson = orderItems.getJSONObject(i)

                            val id_orderItem = orderItemJson.getString("_id")
                            val id_order = orderItemJson.getString("id_order")
                            val item_orderItem = orderItemJson.getString("item")
                            val quantity_orderItem = orderItemJson.getString("quantity")

                            val orderItem = OrderItemModel(id_orderItem, id_order, item_orderItem, quantity_orderItem)
                            orderItemList.add(orderItem)
                        }
                        Log.d("OrderFragment", "recordList: $orderItemList")
                        displayRecords()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(context, error.toString().trim { it <= ' ' }, Toast.LENGTH_SHORT)
                    .show()
            }
        )
        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        requestQueue.add(sr)
    }

    private fun displayRecords() {
        rvOrder.layoutManager = GridLayoutManager(requireContext(), 1)

        val horizontalSpace =resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        val verticalSpace = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        rvOrder.addItemDecoration(SpaceItemDecoration(horizontalSpace, verticalSpace))

        val orderAdapter = OrderAdapter(orderList, orderItemList)
        rvOrder.adapter = orderAdapter
    }
}