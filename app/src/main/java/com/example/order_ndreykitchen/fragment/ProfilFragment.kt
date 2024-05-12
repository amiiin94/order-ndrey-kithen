package com.example.order_ndreykitchen.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.example.order_ndreykitchen.Cart
import com.example.order_ndreykitchen.EditProfile
import com.example.order_ndreykitchen.MainActivity
import com.example.order_ndreykitchen.R

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
    // TODO: Rename and change types of parameters
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
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeItems()

        // Logout
        btn_logout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { dialog, which ->
                    logout()
                }
                .setNegativeButton("Tidak") { dialog, which ->

                }
                .show()
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
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()

        Toast.makeText(requireContext(), "Anda berhasil keluar", Toast.LENGTH_SHORT).show()
    }




}