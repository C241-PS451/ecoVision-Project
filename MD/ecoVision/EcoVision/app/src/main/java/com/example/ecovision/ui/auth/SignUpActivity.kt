package com.example.ecovision.ui.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecovision.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.registerButton.setOnClickListener {
            showLoading(true)
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val username = binding.nameEditText.text.toString().trim()

            if (username.isEmpty()) {
                binding.nameEditTextLayout.error = "Name cannot be empty"
                showLoading(false)
                return@setOnClickListener
            } else {
                binding.nameEditTextLayout.error = null
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEditTextLayout.error = "Invalid email format"
                showLoading(false)
                return@setOnClickListener
            } else {
                binding.emailEditTextLayout.error = null
            }

            if (password.length < 6) {
                binding.passwordEditTextLayout.error = "Password must be at least 6 characters"
                showLoading(false)
                return@setOnClickListener
            } else {
                binding.passwordEditTextLayout.error = null
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    showLoading(false)
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        user?.updateProfile(
                            UserProfileChangeRequest.Builder().setDisplayName(username).build()
                        )
                            ?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    auth.signOut()
                                    Toast.makeText(
                                        this,
                                        "Registration successful.\nPlease log in.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Log.w(TAG, "updateProfile:failure", updateTask.exception)
                                    Toast.makeText(
                                        baseContext,
                                        "Profile update failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        handleSignUpException(task.exception)
                    }
                }
        }

        binding.move.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleSignUpException(exception: Exception?) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                binding.passwordEditTextLayout.error = "Password should be at least 6 characters"
            }

            is FirebaseAuthUserCollisionException -> {
                binding.emailEditTextLayout.error = "Email is already in use"
            }

            else -> {
                Toast.makeText(baseContext, "Registration failed.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "createUserWithEmail:failure", exception)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.registerButton.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.registerButton.isEnabled = true
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}