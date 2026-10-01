package com.aps.lifedonoraps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aps.lifedonoraps.databinding.ActivityAuthBinding
import com.aps.lifedonoraps.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var isLoginMode = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        updateUI()

        binding.tvToggleAuth.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUI()
        }

        binding.btnAction.setOnClickListener {
            handleAuthAction()
        }
    }

    private fun handleAuthAction() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        if (isLoginMode) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    showLoading(false)

                    if (task.isSuccessful) {
                        goToDashboard()
                    } else {
                        Toast.makeText(
                            this,
                            "Login failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {

            val fullName = binding.etFullName.text.toString().trim()
            val bloodGroup = binding.etBloodGroup.text.toString().trim()
            val userType = if (binding.rbDonor.isChecked) "Donor" else "Recipient"

            if (fullName.isEmpty() || bloodGroup.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                showLoading(false)
                return
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val firebaseUser = firebaseAuth.currentUser!!

                        val user = User(
                            uid = firebaseUser.uid,
                            fullName = fullName,
                            email = email,
                            bloodGroup = bloodGroup,
                            userType = userType
                        )

                        firestore.collection("users")
                            .document(firebaseUser.uid)
                            .set(user)
                            .addOnSuccessListener {
                                showLoading(false)
                                goToDashboard()
                            }
                            .addOnFailureListener { e ->
                                showLoading(false)
                                Toast.makeText(
                                    this,
                                    "Failed to save user data: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                    } else {
                        showLoading(false)
                        Toast.makeText(
                            this,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun goToDashboard() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun updateUI() {

        if (isLoginMode) {

            binding.tvTitle.text = getString(R.string.login)
            binding.btnAction.text = getString(R.string.login)
            binding.tvToggleAuth.text = getString(R.string.create_account)

            binding.tilFullName.visibility = View.GONE
            binding.tilBloodGroup.visibility = View.GONE
            binding.tvUserType.visibility = View.GONE
            binding.rgUserType.visibility = View.GONE

        } else {

            binding.tvTitle.text = getString(R.string.register)
            binding.btnAction.text = getString(R.string.register)
            binding.tvToggleAuth.text = getString(R.string.already_have_account)

            binding.tilFullName.visibility = View.VISIBLE
            binding.tilBloodGroup.visibility = View.VISIBLE
            binding.tvUserType.visibility = View.VISIBLE
            binding.rgUserType.visibility = View.VISIBLE
        }
    }
    override fun onStart() {
        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser ?: return

        user.reload()
            .addOnSuccessListener {
                // User still exists on Firebase Authentication.
                goToDashboard()
            }
            .addOnFailureListener {
                // Account was deleted or token is invalid.
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(
                    this,
                    "Please log in again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        binding.btnAction.isEnabled = !isLoading
    }

}
