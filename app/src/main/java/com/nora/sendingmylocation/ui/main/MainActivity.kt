package com.nora.sendingmylocation.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.nora.sendingmylocation.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

private const val REQUEST_CODE_LOCATION = 1

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private var locationPermissionGranted: Boolean = false
    private val DEFAULT_LOCATION = LatLng(13.7245601, 100.4930264)
    private val DEFAULT_ZOOM_LEVEL = 14F
    private val MIN_ZOOM_LEVEL = 6F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        enableMyLocation()
        getUpdateLocation()
        getMyCurrentLocation()

        with(map) {
            setPadding(0, 0, 0, 160)
            setMinZoomPreference(MIN_ZOOM_LEVEL)
            uiSettings.isCompassEnabled = false
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    private fun enableMyLocation() {
        if (hasLocationPermission()) {
            locationPermissionGranted = true
            Timber.i("Enable Location")
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.label_require_location_permission),
                REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            )
            Timber.i("Request Permission")
        }
//        if (ContextCompat.checkSelfPermission(this.applicationContext,
//                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationPermissionGranted = true
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_CODE_LOCATION
//            )
//        }
    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
//        when(requestCode) {
//            REQUEST_CODE_LOCATION -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    locationPermissionGranted = true
//                }
//            }
//        }
        getUpdateLocation()
        getMyCurrentLocation()
    }

    private fun getMyCurrentLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    currentLocation = task.result!!
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(currentLocation!!.latitude, currentLocation!!.longitude), DEFAULT_ZOOM_LEVEL
                        )
                    )
                    Timber.i("Get My Location")
                } else {
                    Timber.e("Exception: ${task.exception}")
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            DEFAULT_LOCATION, DEFAULT_ZOOM_LEVEL
                        )
                    )
                    map.uiSettings.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            Timber.e("Error: ${e.message}")
        }
    }

    private fun getUpdateLocation() {
        try {
            if (locationPermissionGranted) {
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true
                Timber.i("Permission granted = $locationPermissionGranted")
            } else {
                map.isBuildingsEnabled = false
                map.uiSettings.isMyLocationButtonEnabled = false
                currentLocation = null
                Timber.i("Permission granted = $locationPermissionGranted")
            }
        } catch (e: SecurityException) {
            Timber.e("Error: ${e.message}")
        }
    }

    fun onSendMyLocation(view: View) {
        val addressArray = arrayOf("lasmorshe@gmail.com", "s.supol.p@gmail.com")
        composeEmail(addressArray, "Send Email Testing", "test")
    }

    private fun composeEmail(addresses: Array<String>, subject: String, text: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            Toast.makeText(this, "Email has been sent", Toast.LENGTH_SHORT).show()
        }
//        val intent = Intent(Intent.ACTION_SEND).apply {
//            type = "message/rfc822"
//            putExtra(Intent.EXTRA_EMAIL, addresses)
//            putExtra(Intent.EXTRA_SUBJECT, subject)
//            putExtra(Intent.EXTRA_TEXT, text)
//        }
//        if (intent.resolveActivity(packageManager) != null) {
//            startActivity(intent)
//        }
    }
}