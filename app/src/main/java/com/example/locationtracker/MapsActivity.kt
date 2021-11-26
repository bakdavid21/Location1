package com.example.locationtracker


import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.locationtracker.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var fusedLocClient: FusedLocationProviderClient // helyadatok és utolsó tartózkodási hely lekérése
    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    companion object {
        private const val REQUEST_LOCATION = 1
        private const val TAG = "MapsActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupLocationClient()
    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap


        getCurrentLocation()
    }

    private fun setupLocationClient() {
        fusedLocClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION) //helymeghatározás engedélykérés
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions()
        }
        else {
            fusedLocClient.lastLocation.addOnCompleteListener {
                val location = it.result
                val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                val ref: DatabaseReference = database.getReference("test")
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)

                    map.addMarker(MarkerOptions().position(latLng) // Marker létrehozása az adott helyen
                        .title("Itt vagy jelenleg!"))

                    val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)

                    map.moveCamera(update)

                    ref.setValue(location) //helyadat elmentése az adatbázisba
                }
                else {
                    Log.e(TAG, "Hely nem található!")
                }
            }
        }
    }

}