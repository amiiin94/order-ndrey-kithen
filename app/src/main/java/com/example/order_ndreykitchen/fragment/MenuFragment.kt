package com.example.order_ndreykitchen.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.order_ndreykitchen.Adapter.MenuAdapter
import com.example.order_ndreykitchen.Keranjang
import com.example.order_ndreykitchen.MenuDetail
import com.example.order_ndreykitchen.Model.MenuModel
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
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btnKeranjang: Button
    private val menuList = mutableListOf<MenuModel>()
    private lateinit var rv_menu: RecyclerView

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
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizialized
        rv_menu = view.findViewById(R.id.rv_menu)

        // Go to Keranjang
        btnKeranjang = view.findViewById(R.id.btnKeranjang)
        btnKeranjang.setOnClickListener {
            val intent = Intent(requireContext(), Keranjang::class.java)
            startActivity(intent)
        }

        getAllMenus(requireContext())


    }

    private fun getAllMenus(context: Context) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/getAllMenus"
        val sr = StringRequest(
            Request.Method.GET,
            urlEndPoints,
            { response ->
                try {
                    menuList.clear()
                    val menus = JSONArray(response)
                    for (i in 0 until menus.length()) {
                        val menuJson = menus.getJSONObject(i)

                        val id_menu = menuJson.getString("_id")
                        val nama_menu = menuJson.getString("nama")
                        val harga_menu = menuJson.getInt("harga")
                        val images = menuJson.getString("image")
                        val deskripsi_menu = menuJson.getString("deskripsi")
                        val kategori_menu = menuJson.getString("kategori")


                        val menu = MenuModel(id_menu, nama_menu, harga_menu, images, deskripsi_menu, kategori_menu)
                        menuList.add(menu)
                    }
                    Log.d("MenuFragment", "menuList: $menuList")
                    displayMenu()
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

    private fun displayMenu() {
        rv_menu.layoutManager = GridLayoutManager(requireContext(), 2)

        val horizontalSpace =resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        val verticalSpace = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        rv_menu.addItemDecoration(SpaceItemDecoration(horizontalSpace, verticalSpace))

        val menuAdapter = MenuAdapter(menuList)
        rv_menu.adapter = menuAdapter
    }
}