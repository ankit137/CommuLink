package com.example.resolute_assignment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.util.GeoPoint

class location : AppCompatActivity() {
    private lateinit var mapView: MapView
    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private lateinit var locatorButton: Button
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            addLocationOverlay()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        // Initialize OSMDroid configuration
        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))

        // Set up the map view
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Add scale bar overlay
        val scaleBarOverlay = ScaleBarOverlay(mapView)
        mapView.overlays.add(scaleBarOverlay)

        // Add compass overlay
        val compassOverlay = CompassOverlay(this, mapView)
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)
        locatorButton = findViewById(R.id.locator)
        locatorButton.setOnClickListener {
            centerMapOnCurrentLocation()
        }
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                locationPermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            addLocationOverlay()
        } else {
            requestPermissionLauncher.launch(locationPermission)
        }
    }

    private fun addLocationOverlay() {
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), mapView)
        locationOverlay.enableMyLocation()
        mapView.overlays.add(locationOverlay)

        // Center the map on the current location
        centerMapOnCurrentLocation()
    }

    private fun centerMapOnCurrentLocation() {
        val locationOverlay = mapView.overlays.firstOrNull { it is MyLocationNewOverlay } as MyLocationNewOverlay?
        if (locationOverlay != null && locationOverlay.isEnabled) {
            val myLocation = locationOverlay.myLocation
            if (myLocation != null) {
                mapView.controller.setCenter(myLocation)
                mapView.controller.setZoom(16)
            }
        }
    }
}
