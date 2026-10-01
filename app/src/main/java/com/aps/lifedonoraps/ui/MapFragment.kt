package com.aps.lifedonoraps.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aps.lifedonoraps.R
import com.aps.lifedonoraps.model.User
import com.aps.lifedonoraps.util.DistanceUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var currentLat = 0.0
    private var currentLng = 0.0

    companion object {
        private const val SEARCH_RADIUS_KM = 15.0
    }

    // -------------------- Permission Launcher --------------------

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                enableMyLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required to find nearby donors.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // -------------------- Fragment Lifecycle --------------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_container) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        checkPermissionAndLocate()
    }

    // -------------------- Location Permission --------------------

    private fun checkPermissionAndLocate() {

        val finePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (finePermission == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // -------------------- Get Current Location --------------------

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {

        googleMap?.isMyLocationEnabled = true

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location == null) {
                    Toast.makeText(
                        requireContext(),
                        "Unable to get current location. Turn on GPS.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnSuccessListener
                }

                currentLat = location.latitude
                currentLng = location.longitude

                val myLocation = LatLng(currentLat, currentLng)

                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(myLocation, 13f)
                )

                googleMap?.addMarker(
                    MarkerOptions()
                        .position(myLocation)
                        .title("You are here")
                        .icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_AZURE
                            )
                        )
                )

                saveMyLocation(currentLat, currentLng)
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch location.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // -------------------- Save My Location to Firestore --------------------

    private fun saveMyLocation(lat: Double, lng: Double) {

        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .update(
                mapOf(
                    "latitude" to lat,
                    "longitude" to lng
                )
            )
            .addOnSuccessListener {
                loadNearbyDonors()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Location update failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // -------------------- Load Nearby Donors --------------------

    private fun loadNearbyDonors() {

        val myUid = auth.currentUser?.uid ?: return

        googleMap?.clear()

        val myLocation = LatLng(currentLat, currentLng)

        googleMap?.addMarker(
            MarkerOptions()
                .position(myLocation)
                .title("You are here")
                .icon(
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_AZURE
                    )
                )
        )

        firestore.collection("users")
            .whereEqualTo("userType", "Donor")
            .whereEqualTo("available", true)
            .get()
            .addOnSuccessListener { snapshot ->

                var donorCount = 0

                for (doc in snapshot.documents) {

                    val donor = doc.toObject(User::class.java) ?: continue

                    // Skip myself
                    if (donor.uid == myUid) continue

                    // Skip donors without location
                    if (donor.latitude == 0.0 && donor.longitude == 0.0) continue

                    val distance = DistanceUtils.haversineKm(
                        currentLat,
                        currentLng,
                        donor.latitude,
                        donor.longitude
                    )

                    if (distance <= SEARCH_RADIUS_KM) {

                        donorCount++

                        googleMap?.addMarker(
                            MarkerOptions()
                                .position(LatLng(donor.latitude, donor.longitude))
                                .title("${donor.fullName} (${donor.bloodGroup})")
                                .snippet(
                                    String.format(
                                        "%.1f km away • %d donations • %d pts",
                                        distance,
                                        donor.donationCount,
                                        donor.points
                                    )
                                )
                                .icon(
                                    BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_RED
                                    )
                                )
                        )
                    }
                }

                Toast.makeText(
                    requireContext(),
                    "$donorCount donor(s) found within ${SEARCH_RADIUS_KM.toInt()} km.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to load donors: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}