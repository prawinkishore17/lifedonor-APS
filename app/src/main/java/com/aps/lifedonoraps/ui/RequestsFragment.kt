package com.aps.lifedonoraps.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aps.lifedonoraps.R
import com.aps.lifedonoraps.model.BloodRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * Live-updating list of open blood requests.
 * Uses a Firestore snapshot listener, so new requests appear
 * without any manual refresh.
 */
class RequestsFragment : Fragment(R.layout.fragment_requests) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RequestAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView

    private val firestore = FirebaseFirestore.getInstance()
    private var listener: com.google.firebase.firestore.ListenerRegistration? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvRequests)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)

        adapter = RequestAdapter(mutableListOf()) { request -> dialContact(request.contact) }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        view.findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(requireContext(), PostRequestActivity::class.java))
        }

        attachRealtimeListener()
    }

    private fun attachRealtimeListener() {
        progressBar.visibility = View.VISIBLE

        listener = firestore.collection("bloodRequests")
            .whereEqualTo("status", "Open")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                progressBar.visibility = View.GONE

                if (error != null) {
                    Toast.makeText(requireContext(),
                        "Error loading requests: ${error.message}",
                        Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(BloodRequest::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                adapter.updateData(list)
                tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
    }

    private fun dialContact(number: String) {
        if (number.isBlank()) return
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener?.remove()
    }
}
