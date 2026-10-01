package com.aps.lifedonoraps.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.aps.lifedonoraps.R
import com.aps.lifedonoraps.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvWelcome: TextView = view.findViewById(R.id.tvWelcome)
        val tvPoints: TextView = view.findViewById(R.id.tvPointsValue)
        val tvOpenRequests: TextView = view.findViewById(R.id.tvOpenRequestsValue)
        val tvBloodGroup: TextView = view.findViewById(R.id.tvBloodGroupValue)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("users").document(uid)
            .addSnapshotListener { doc, _ ->
                val user = doc?.toObject(User::class.java) ?: return@addSnapshotListener
                tvWelcome.text = "Welcome, ${user.fullName}"
                tvPoints.text = user.points.toString()
                tvBloodGroup.text = user.bloodGroup

                // Count open requests matching this user's blood group
                firestore.collection("bloodRequests")
                    .whereEqualTo("status", "Open")
                    .whereEqualTo("bloodGroup", user.bloodGroup)
                    .addSnapshotListener { snap, _ ->
                        tvOpenRequests.text = (snap?.size() ?: 0).toString()
                    }
            }
    }
}
