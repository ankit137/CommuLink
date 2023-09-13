package com.example.resolute_assignment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit


class PhoneAuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var userMobile: String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var verificationId: String? = null
    private var useremail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)
        useremail = intent.getStringExtra("useremail")
        userMobile = intent.getStringExtra("usermobile")
        auth = FirebaseAuth.getInstance()

        val sendOtpButton: Button = findViewById(R.id.verifyButton)
        val otpEditText: EditText = findViewById(R.id.otpEditText)
        val enternumber: EditText = findViewById(R.id.enternumber)
        val getotp: Button = findViewById(R.id.getotp)

        setupPhoneAuthCallbacks()

        // Set up a click listener for the "Get OTP" button
        getotp.setOnClickListener {
            // Get the entered mobile number from the EditText
            val phoneNumber = "+91${enternumber.text.toString().trim()}"

            if (phoneNumber.isNotEmpty()) {
                // Trigger the OTP verification process by sending a verification code
                sendVerificationCode(phoneNumber)
                userMobile = phoneNumber // Update userMobile with the entered number
            } else {
                // Display an error message indicating that the mobile number is empty
                showToast("Please enter your mobile number.")
            }
        }

        sendOtpButton.setOnClickListener {
            // Get the entered OTP code from the EditText
            val otpCode = otpEditText.text.toString().trim()

            if (otpCode.isNotEmpty()) {
                // Validate and verify the OTP code
                verifyPhoneNumberWithCode(otpCode)
            } else {
                // Display an error message indicating that the OTP code is empty
                showToast("Please enter the OTP code.")
            }
        }
    }

    private fun verifyPhoneNumberWithCode(otpCode: String) {
        // Use Firebase to verify the OTP code and sign in the user
        val credential = PhoneAuthProvider.getCredential(verificationId!!, otpCode)
        signInWithPhoneAuthCredential(credential)
    }

    private fun setupPhoneAuthCallbacks() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked when verification is completed automatically.
                // You can sign in the user here.
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked when verification fails.
                // Handle verification failure here.
                showToast("Verification failed: ${e.message}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // This callback is invoked when the verification code is successfully sent.
                // You can store the verificationId and token for later use.
                this@PhoneAuthActivity.verificationId = verificationId
                showToast("OTP sent to your phone.")
                val enterOtpTextView = findViewById<TextView>(R.id.enterotp)
                val otpEditText = findViewById<EditText>(R.id.otpEditText)
                val verifyButton = findViewById<Button>(R.id.verifyButton)

                enterOtpTextView.visibility = View.VISIBLE
                otpEditText.visibility = View.VISIBLE
                verifyButton.visibility = View.VISIBLE
            }
        }
    }


    private fun sendVerificationCode(phoneNumber: String) {
        // Create and send the verification code to the provided phone number
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Phone number authentication successful, handle it here
                    showToast("Phone number authentication successful.")

                    // Start the profile_page activity
                    val intent = Intent(this@PhoneAuthActivity, profile_page::class.java)
                    intent.putExtra("useremail", useremail)
                    intent.putExtra("usernumber", userMobile)
                    startActivity(intent)
                    finish()
                } else {
                    // Phone number authentication failed, handle it here
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        showToast("Invalid OTP code.")
                    } else {
                        showToast("Phone authentication failed.")
                    }
                }
            }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}



