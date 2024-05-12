package com.example.order_ndreykitchen.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.order_ndreykitchen.Adapter.MenuAdapter
import com.example.order_ndreykitchen.Login
import com.example.order_ndreykitchen.MainActivity
import com.example.order_ndreykitchen.Model.MenuModel
import com.example.order_ndreykitchen.R
import com.example.order_ndreykitchen.SpaceItemDecoration
import org.json.JSONArray
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [BerandaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BerandaFragment : Fragment() {

    private lateinit var horizontalview : HorizontalScrollView
    private lateinit var picture: TextView
    private lateinit var tvLihatSemua: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etSearch: EditText
    private val menuList = mutableListOf<MenuModel>()
    private lateinit var rvMenu: RecyclerView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beranda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        horizontalview = view.findViewById(R.id.horizontalScrollView2)
        picture = view.findViewById(R.id.picture)
        tvLihatSemua = view.findViewById(R.id.tvLihatSemua)
        etSearch = view.findViewById(R.id.etSearch)
        rvMenu = view.findViewById(R.id.rvMenu)

        // Start auto-scroll
        startAutoScroll(horizontalview)

        // Set picture based on user's full name
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val fullname = sharedPreferences.getString("fullname_user", "")
        if (!fullname.isNullOrEmpty()) {
            picture.text = fullname[0].toString().uppercase()
        } else {
            picture.visibility = View.GONE
        }

        // Handle clicks on picture
        picture.setOnClickListener {
            if (fullname.isNullOrEmpty()) {
                val intent = Intent(requireContext(), Login::class.java)
                startActivity(intent)
            } else {
                (requireActivity() as MainActivity).binding.bottomNavigation.selectedItemId = R.id.profil
            }
        }

        // Handle clicks on "Lihat Semua"
        tvLihatSemua.setOnClickListener {
            (requireActivity() as MainActivity).binding.bottomNavigation.selectedItemId = R.id.menu
        }

        // Handle search action
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val searchQuery = etSearch.text.toString()
                // Navigate to MenuFragment and pass the search query
                val menuFragment = MenuFragment.newInstance(searchQuery)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, menuFragment)
                    .addToBackStack(null)
                    .commit()
                return@setOnEditorActionListener true
            }
            false
        }
        get5PopularMenu(requireContext())

    }


    private fun startAutoScroll(horizontalScrollView: HorizontalScrollView) {
        val scrollMax = horizontalScrollView.getChildAt(0).width - horizontalScrollView.width
        val duration = 5000L // Adjust duration as needed

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (horizontalScrollView.scrollX < scrollMax) {
                    horizontalScrollView.smoothScrollBy(1, 0)
                    handler.postDelayed(this, duration)
                } else {
                    // Reset scroll to start position
                    horizontalScrollView.scrollTo(0, 0)
                    handler.postDelayed(this, duration)
                }
            }
        }
        handler.postDelayed(runnable, duration)
    }

    private fun get5PopularMenu(context: Context) {
        val urlEndPoints = "https://ap-southeast-1.aws.data.mongodb-api.com/app/application-0-kofjt/endpoint/get5PopularMenu"
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
        // Set LinearLayoutManager with horizontal orientation and 1 column
        rvMenu.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        // Set adapter for RecyclerView
        val menuAdapter = MenuAdapter(menuList)
        rvMenu.adapter = menuAdapter

        // Add space between items in RecyclerView
        val horizontalSpaceInPixels = resources.getDimensionPixelSize(R.dimen.horizontal_space_between_items)
        val verticalSpaceInPixels = resources.getDimensionPixelSize(R.dimen.vertical_space_between_items)
        rvMenu.addItemDecoration(SpaceItemDecoration(horizontalSpaceInPixels, verticalSpaceInPixels))
    }

}