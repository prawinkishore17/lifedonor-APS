package com.aps.lifedonoraps.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aps.lifedonoraps.databinding.ActivityPostRequestBinding
import com.aps.lifedonoraps.model.BloodRequest
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostRequestBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Post Blood Request"

        binding.btnSubmit.setOnClickListener { submitRequest() }
    }

    private fun submitRequest() {
        val patient = binding.etPatientName.text.toString().trim()
        val bloodGroup = binding.etBloodGroup.text.toString().trim().uppercase()
        val hospital = binding.etHospital.text.toString().trim()
        val contact = binding.etContact.text.toString().trim()
        val urgency = if (binding.cbUrgent.isChecked) "Urgent" else "Normal"

        if (patient.isEmpty() || bloodGroup.isEmpty() ||
            hospital.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "You must be logged in", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val request = BloodRequest(
            patientName = patient,
            bloodGroup = bloodGroup,
            hospital = hospital,
            contact = contact,
            urgency = urgency,
            status = "Open",
            requesterId = user.uid,
            requesterName = user.email ?: "",
            createdAt = Timestamp.now()
        )

        firestore.collection("bloodRequests")
            .add(request)
            .addOnSuccessListener {
                showLoading(false)
                Toast.makeText(this, "Request posted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                showLoading(false)
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !loading
    }
}
