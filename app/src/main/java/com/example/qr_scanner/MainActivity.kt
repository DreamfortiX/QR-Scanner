package com.example.qr_scanner

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.linear)



        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Check if savedInstanceState is null to set the initial fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, Generate()) // Set your initial fragment
                .commit()
        }


        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_QR -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, Generate())
                        // Navigate to Generate fragment
                        .commit()
                    bottomNavigationView.itemBackgroundResource = R.color.transparent_lavender

                    true
                }

                R.id.nav_History -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, History()) // Navigate to History fragment
                        .commit()
                    bottomNavigationView.itemBackgroundResource = R.color.transparent_lavender
                    true
                }
                // Add other cases if needed
                else -> false
            }
        }

        // Set up the Floating Action Button (FAB)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, Scanner()) // Navigate to QrScannerFragment
                .addToBackStack(null) // Optional: Add to back stack to allow back navigation
                .commit()

        }

    }

}