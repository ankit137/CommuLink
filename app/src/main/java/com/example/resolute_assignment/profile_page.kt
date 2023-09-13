package com.example.resolute_assignment

import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class profile_page : AppCompatActivity() {
    private val REQUEST_LOCATION_ENABLE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        val livelocationbutton = findViewById<Button>(R.id.liveloactionbutton)
        val callingButton = findViewById<Button>(R.id.calling)

        // Check if location services are enabled
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Location services are not enabled, request the user to enable them
            val enableLocationIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(enableLocationIntent, REQUEST_LOCATION_ENABLE)
        } else {
            // Location services are already enabled, continue with the activity
            initActivity()
        }

        livelocationbutton.setOnClickListener {
            val intent = Intent(this@profile_page, location::class.java)
            startActivity(intent)
        }

        callingButton.setOnClickListener {
            val intent = Intent(this@profile_page, calling::class.java)
            startActivity(intent)
        }

        val userEmail = intent.getStringExtra("useremail")
        val userMobile = intent.getStringExtra("usernumber")

        val textViewUserEmail = findViewById<TextView>(R.id.textViewUserEmail)
        val textViewUserMobile = findViewById<TextView>(R.id.textViewUserMobile)

        textViewUserEmail.text = "User Email: $userEmail"
        textViewUserMobile.text = "User Mobile number: $userMobile"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_LOCATION_ENABLE) {
            // Check if the user enabled location services
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Location services were enabled, continue with the activity
                initActivity()
            } else {
                // Location services were not enabled, show a message to the user
                Toast.makeText(this, "Location services must be enabled to use this feature.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initActivity() {
        // Initialize the activity when location services are enabled
    }
}
