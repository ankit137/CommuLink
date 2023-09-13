package com.example.resolute_assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var emailid: String="alphagamer8700@gmail.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Check if the user is already authenticated
        if (auth.currentUser != null) {
            // User is already logged in, navigate to phone number authentication
            startPhoneNumberAuthentication()
            finish()
        }

        val loginButton: Button = findViewById(R.id.loginButton)
        val emailEditText: EditText = findViewById(R.id.username)
        val passwordEditText: EditText = findViewById(R.id.password)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Attempt email/password authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Email/password login successful, start phone number authentication
                        emailid=email
                        startPhoneNumberAuthentication()
                        finish()
                    } else {
                        // Email/password login failed, check if it's due to invalid user
                        if (task.exception is FirebaseAuthInvalidUserException) {
                            // If user is not found, display an error message
                            Toast.makeText(this, "Invalid user. Check your credentials.", Toast.LENGTH_SHORT).show()
                        } else {
                            // Display an error message for other login failures
                            Toast.makeText(this, "Authentication failed. Check your credentials.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun startPhoneNumberAuthentication() {
        // Implement phone number authentication logic here
        // You can start the phone number authentication process and handle it accordingly
        // Example: Navigate to PhoneAuthActivity for phone number verification
        val intent = Intent(this, PhoneAuthActivity::class.java)
        intent.putExtra("useremail",emailid)
        intent.putExtra("usermobile", "+91 XXXXX XXXXX")
        startActivity(intent)
    }
}
