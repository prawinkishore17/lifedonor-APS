package com.aps.lifedonoraps

import android.os.Bundle
import android.util.Log              // <-- ADD THIS IMPORT
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.aps.lifedonoraps.databinding.ActivityDashboardBinding
import com.aps.lifedonoraps.ui.*
import com.google.firebase.auth.FirebaseAuth   // <-- ADD THIS IMPORT

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ===== DEBUG: Print logged-in Firebase user =====
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("AUTH_CHECK", "UID = ${user?.uid}")
        Log.d("AUTH_CHECK", "EMAIL = ${user?.email}")
        // ===============================================

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_dashboard -> HomeFragment()
                R.id.nav_map -> MapFragment()
                R.id.nav_requests -> RequestsFragment()
                R.id.nav_leaderboard -> LeaderboardFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> return@setOnItemSelectedListener false
            }

            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}