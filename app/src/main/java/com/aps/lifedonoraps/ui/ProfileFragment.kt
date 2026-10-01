package com.aps.lifedonoraps.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.aps.lifedonoraps.AuthActivity
import com.aps.lifedonoraps.R
import com.aps.lifedonoraps.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvBloodGroup: TextView = view.findViewById(R.id.tvBloodGroup)
        val tvUserType: TextView = view.findViewById(R.id.tvUserType)
        val tvPoints: TextView = view.findViewById(R.id.tvPoints)
        val tvDonations: TextView = view.findViewById(R.id.tvDonations)
        val switchAvailable: SwitchCompat = view.findViewById(R.id.switchAvailable)
        val btnLogout: Button = view.findViewById(R.id.btnLogout)

        val uid = auth.currentUser?.uid

        if (uid != null) {
            firestore.collection("users").document(uid)
                .addSnapshotListener { doc, error ->
                    if (error != null || doc == null || !doc.exists()) return@addSnapshotListener
                    val user = doc.toObject(User::class.java) ?: return@addSnapshotListener

                    tvName.text = user.fullName
                    tvEmail.text = user.email
                    tvBloodGroup.text = user.bloodGroup
                    tvUserType.text = user.userType
                    tvPoints.text = user.points.toString()
                    tvDonations.text = user.donationCount.toString()
                    switchAvailable.isChecked = user.available
                }

            switchAvailable.setOnCheckedChangeListener { _, isChecked ->
                firestore.collection("users").document(uid)
                    .update("available", isChecked)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(),
                            if (isChecked) "You are now visible to recipients"
                            else "You are now hidden from search",
                            Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
