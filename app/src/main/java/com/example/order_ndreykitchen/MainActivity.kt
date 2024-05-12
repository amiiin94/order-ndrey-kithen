package com.example.order_ndreykitchen

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.order_ndreykitchen.databinding.ActivityMainBinding
import com.example.order_ndreykitchen.fragment.BerandaFragment
import com.example.order_ndreykitchen.fragment.MenuFragment
import com.example.order_ndreykitchen.fragment.ProfilFragment
import com.example.order_ndreykitchen.fragment.RiwayatFragment


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Apply window insets to the root layout
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val berandaFragment = BerandaFragment()
        val menuFragment = MenuFragment()
        val riwayatFragment = RiwayatFragment()
        val profilFragment = ProfilFragment()




        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val email_user = sharedPreferences.getString("email_user", "")

        // Set up bottom navigation listener
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.beranda -> {
                    replaceFragment(BerandaFragment())
                    true
                }
                R.id.menu -> {
                    replaceFragment(MenuFragment())
                    true
                }
                R.id.riwayat -> {
                    if (email_user.isNullOrEmpty()) {
                        // Email kosong, arahkan ke layar login
                        startActivity(Intent(this, Login::class.java))
                    } else {
                        replaceFragment(RiwayatFragment())
                    }
                    true
                }
                R.id.profil -> {
                    if (email_user.isNullOrEmpty()) {
                        // Email kosong, arahkan ke layar login
                        startActivity(Intent(this, Login::class.java))
                    } else {
                        replaceFragment(ProfilFragment())
                    }
                    true
                }
                else -> false
            }
        }

        // Initially, display the BerandaFragment
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.beranda
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


}
