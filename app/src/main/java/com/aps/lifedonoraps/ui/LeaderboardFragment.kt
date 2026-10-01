package com.aps.lifedonoraps.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aps.lifedonoraps.R
import com.aps.lifedonoraps.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * Donor recognition leaderboard - ranks donors by accumulated reward points.
 */
class LeaderboardFragment : Fragment(R.layout.fragment_leaderboard) {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: LeaderboardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv: RecyclerView = view.findViewById(R.id.rvLeaderboard)
        val tvMyRank: TextView = view.findViewById(R.id.tvMyRank)

        adapter = LeaderboardAdapter(mutableListOf())
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        val myUid = FirebaseAuth.getInstance().currentUser?.uid

        firestore.collection("users")
            .whereEqualTo("userType", "Donor")
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val donors = snapshot.documents.mapNotNull {
                    it.toObject(User::class.java)
                }
                adapter.updateData(donors)

                val myIndex = donors.indexOfFirst { it.uid == myUid }
                tvMyRank.text = if (myIndex >= 0) {
                    "Your rank: #${myIndex + 1}  -  ${donors[myIndex].points} points"
                } else {
                    "Donate to appear on the leaderboard"
                }
            }
    }
}
