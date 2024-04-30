package com.example.order_ndreykitchen.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import com.example.order_ndreykitchen.Login
import com.example.order_ndreykitchen.MainActivity
import com.example.order_ndreykitchen.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [BerandaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BerandaFragment : Fragment() {

    private lateinit var horizontalview : HorizontalScrollView
    private lateinit var iv_pp: ImageView
    private lateinit var tvLihatSemua: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beranda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        horizontalview = view.findViewById(R.id.horizontalScrollView2)
        startAutoScroll(horizontalview)

        iv_pp = view.findViewById(R.id.iv_pp)
        iv_pp.setOnClickListener {
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
        }

        // Klik Lihat Semua
        tvLihatSemua = view.findViewById(R.id.tvLihatSemua)
        tvLihatSemua.setOnClickListener {
            // Change the selected item in the bottom navigation to R.id.menu
            (requireActivity() as MainActivity).binding.bottomNavigation.selectedItemId = R.id.menu
        }
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

    companion object {
        @JvmStatic
        fun newInstance() =
            BerandaFragment().apply {
                // No need to pass any arguments
            }
    }
}