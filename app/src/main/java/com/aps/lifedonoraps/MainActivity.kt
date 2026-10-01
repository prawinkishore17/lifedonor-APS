package com.aps.lifedonoraps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Handler to wait for 2 seconds (splash screen)
        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // User is signed in, go to Dashboard
                startActivity(Intent(this, DashboardActivity::class.java))
            } else {
                // No user is signed in, go to Auth screen
                startActivity(Intent(this, AuthActivity::class.java))
            }
            finish() // Finish this activity so user can't go back to it
        }, 2000)
    }
}