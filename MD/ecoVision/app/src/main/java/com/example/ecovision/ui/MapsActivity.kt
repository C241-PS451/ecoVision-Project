package com.example.ecovision.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ecovision.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ecovision.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var recyclingLocations: List<RecyclingLocation>
    private var userLocation: Location? = null
    private var scannedPlasticType: String? = null
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        scannedPlasticType = intent.getStringExtra("scannedPlasticType")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        // Request location permissions
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            mMap.isMyLocationEnabled = true
            getUserLocation()
        }

        // Add recycling locations
        addRecyclingLocations()
    }

    private fun getUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = location
                    val userLatLng = LatLng(location.latitude, location.longitude)

                    boundsBuilder.include(userLatLng)
                    // Move camera directly to user's location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 13f))
                    showNearbyLocations()
                } else {
                    animateToDefaultLocation()
                }
            }.addOnFailureListener {
                animateToDefaultLocation()
            }
        }
    }

    private fun addRecyclingLocations() {
        // Dummy data for recycling locations
        recyclingLocations = listOf(
            RecyclingLocation(
                LatLng(-6.2607, 106.7816),
                "Jakarta",
                "Waste4Change Recycling Center"
            ),
            RecyclingLocation(LatLng(-6.2008, 106.8525), "Jakarta", "Green Project Jakarta"),
            RecyclingLocation(LatLng(-6.2288, 106.8256), "Jakarta", "Project Jakarta"),
            RecyclingLocation(LatLng(-6.1399, 106.8694), "Jakarta", "EcoWaste Solutions Jakarta"),
            RecyclingLocation(LatLng(-6.2921, 106.7972), "Jakarta", "Zero Waste Jakarta Hub"),
            RecyclingLocation(LatLng(-6.1744, 106.8300), "Jakarta", "Recycle Jakarta Center"),
            RecyclingLocation(LatLng(-7.2575, 112.7521), "Surabaya", "EcoRecycle Surabaya"),
            RecyclingLocation(LatLng(-7.2675, 112.7421), "Surabaya", "Eco pak eco"),
            RecyclingLocation(LatLng(-7.2796, 112.7387), "Surabaya", "Green Waste Hub"),
            RecyclingLocation(LatLng(-7.3123, 112.7274), "Surabaya", "Zero Waste Surabaya"),
            RecyclingLocation(LatLng(-7.2975, 112.7121), "Surabaya", "Cape bgt capstone"),
            RecyclingLocation(
                LatLng(-7.2655, 112.7151),
                "Surabaya",
                "Recycle Center East Surabaya"
            ),
            RecyclingLocation(LatLng(-7.3068, 112.6891), "Surabaya", "Sustainable Waste Solutions"),
            RecyclingLocation(LatLng(-8.6905, 115.1926), "Denpasar", "Bali Bersih"),
            RecyclingLocation(
                LatLng(-8.6793, 115.2253),
                "Denpasar",
                "Green School Bali Recycling Hub"
            ),
            RecyclingLocation(LatLng(-8.6878, 115.1861), "Denpasar", "Zero Waste Bali"),
            RecyclingLocation(LatLng(-8.6508, 115.2307), "Denpasar", "Recycle Indonesia Denpasar"),
            RecyclingLocation(
                LatLng(-8.7094, 115.2212),
                "Denpasar",
                "Bali Waste Management Solutions"
            ),
            RecyclingLocation(LatLng(3.5952, 98.6722), "Medan", "Medan GoGreen"),
            RecyclingLocation(LatLng(3.5897, 98.6750), "Medan", "Sustainable Waste Center"),
            RecyclingLocation(LatLng(3.5850, 98.6922), "Medan", "EcoWaste Solutions Medan"),
            RecyclingLocation(LatLng(3.5953, 98.6781), "Medan", "Green Recycling Hub"),
            RecyclingLocation(LatLng(3.6352, 98.6322), "Medan", "Daur Ulang PD"),
            RecyclingLocation(
                LatLng(-0.0275, 109.3425),
                "Pontianak",
                "Bank Sampah Pontianak Barat"
            ),
            RecyclingLocation(
                LatLng(-0.0890, 109.3098),
                "Pontianak",
                "Green Waste Management Pontianak"
            ),
            RecyclingLocation(LatLng(-0.0358, 109.3695), "Pontianak", "Zero Waste Pontianak"),
            RecyclingLocation(LatLng(-3.3186, 114.5900), "Banjarmasin", "Bank Sampah Banua Bersih"),
            RecyclingLocation(LatLng(-3.3225, 114.6017), "Banjarmasin", "Eco Recycle Banjarmasin"),
            RecyclingLocation(
                LatLng(-3.3133, 114.6031),
                "Banjarmasin",
                "Green Project Banjarmasin"
            ),
            RecyclingLocation(LatLng(-5.1477, 119.4327), "Makassar", "Bank Sampah Makassar"),
            RecyclingLocation(
                LatLng(-5.1368, 119.4394),
                "Makassar",
                "EcoMakassar Recycling Center"
            ),
            RecyclingLocation(LatLng(-5.1459, 119.4740), "Makassar", "Zero Waste Makassar"),
            RecyclingLocation(LatLng(-6.8561817, 107.5840618), "Bandung", "Daur Ulang bro"),
            RecyclingLocation(LatLng(-6.8656, 107.5957), "Bandung", "Parongpong Recycle Centre"),
            RecyclingLocation(LatLng(-6.9175, 107.6191), "Bandung", "Mallsampah Recycling Network"),
            RecyclingLocation(LatLng(-6.9380, 107.6040), "Bandung", "Bank Sampah Bersinar"),
            RecyclingLocation(LatLng(-6.9035, 107.6207), "Bandung", "EcoWaste Management"),
            RecyclingLocation(LatLng(-6.9373, 107.6523), "Bandung", "Green Project Indonesia"),
            RecyclingLocation(LatLng(-6.9223, 107.6340), "Bandung", "Waste4Change"),
            RecyclingLocation(LatLng(-6.9695238, 107.6158372), "Bandung", "Yoyo Botol"),
            RecyclingLocation(LatLng(-6.9940003, 107.592551), "Bandung", "Bank Sampah Bersinar"),
            RecyclingLocation(LatLng(-6.9644011, 107.5448667), "Bandung", "Bandung Waste Daur"),
            RecyclingLocation(LatLng(-6.9121, 107.6365), "Bandung", "Kang Pisman Program"),
            RecyclingLocation(LatLng(-6.9344, 107.6188), "Bandung", "Zero Waste Center"),
            RecyclingLocation(LatLng(-7.7956, 110.3695), "Yogyakarta", "Bank Sampah Gemah Ripah"),
            RecyclingLocation(LatLng(-7.7829, 110.4086), "Yogyakarta", "Green Project Jogja"),
            RecyclingLocation(LatLng(-7.7973, 110.4091), "Yogyakarta", "EcoJogja Recycling Center"),
            RecyclingLocation(LatLng(-7.7589, 110.3577), "Yogyakarta", "Zero Waste Yogyakarta"),
            RecyclingLocation(LatLng(-7.8317, 110.3687), "Yogyakarta", "Recycle Station Jogja"),
            RecyclingLocation(LatLng(-7.7713, 110.3912), "Yogyakarta", "Bank Sampah Mutiara"),
            RecyclingLocation(
                LatLng(-6.9730, 110.4169),
                "Semarang",
                "Paragon Mall Recycling Center"
            ),
            RecyclingLocation(LatLng(-6.9840, 110.4146), "Semarang", "Banyumanik Waste Bank"),
            RecyclingLocation(LatLng(-7.0051, 110.4381), "Semarang", "Green Waste Indonesia"),
            RecyclingLocation(LatLng(-7.0020, 110.4384), "Semarang", "Bank Sampah Bina Mandiri"),
            RecyclingLocation(LatLng(-6.9824, 110.4070), "Semarang", "Zero Waste Indonesia"),
            RecyclingLocation(LatLng(-7.0232, 110.4325), "Semarang", "EcoHub Semarang"),
            RecyclingLocation(LatLng(-6.9855, 110.4213), "Semarang", "Recycle Station Semarang"),
            RecyclingLocation(LatLng(-0.9492, 100.3543), "Padang", "Bank Sampah Induk Kota Padang"),
            RecyclingLocation(LatLng(-0.9227, 100.3603), "Padang", "Green Project Padang"),
            RecyclingLocation(LatLng(-0.9571, 100.3899), "Padang", "EcoWaste Solutions"),
            RecyclingLocation(LatLng(-0.9242, 100.3420), "Padang", "Zero Waste Padang"),
            RecyclingLocation(LatLng(-0.9451, 100.4038), "Padang", "Recycle Center Padang"),
            RecyclingLocation(LatLng(-0.9693, 100.3525), "Padang", "Bank Sampah Bersih Padang")
        )

        // Add markers for each recycling location
        for (location in recyclingLocations) {
            mMap.addMarker(
                MarkerOptions().position(location.latLng).title(location.city)
                    .snippet("Tempat recycle: ${location.plasticType}")
            )
            boundsBuilder.include(location.latLng)
        }
    }

    private fun showNearbyLocations() {
        if (userLocation != null && scannedPlasticType != null) {
            mMap.clear()
            val userLatLng = LatLng(userLocation!!.latitude, userLocation!!.longitude)

            boundsBuilder.include(userLatLng)

            // Display nearby locations based on the scanned plastic type
            val nearbyLocations = getNearbyLocations(userLatLng, scannedPlasticType!!)
            for (location in nearbyLocations) {
                mMap.addMarker(
                    MarkerOptions().position(location.latLng).title(location.city)
                        .snippet("Tempat recycle: ${location.plasticType}")
                )
                boundsBuilder.include(location.latLng)
            }

            animateToShowAllMarkers()
        }
    }

    private fun getNearbyLocations(
        userLatLng: LatLng,
        plasticType: String
    ): List<RecyclingLocation> {
        val nearbyLocations = mutableListOf<RecyclingLocation>()
        val radius = 10000 // Radius in meters (10 km)

        for (location in recyclingLocations) {
            if (location.plasticType == plasticType) {
                val distance = getDistance(userLatLng, location.latLng)
                if (distance <= radius) {
                    nearbyLocations.add(location)
                }
            }
        }

        return nearbyLocations
    }

    private fun getDistance(latLng1: LatLng, latLng2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            latLng1.latitude,
            latLng1.longitude,
            latLng2.latitude,
            latLng2.longitude,
            results
        )
        return results[0]
    }

    private fun animateToShowAllMarkers() {
        try {
            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        } catch (e: IllegalStateException) {
            animateToDefaultLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mMap.isMyLocationEnabled = true
                    getUserLocation()
                }
            } else {
                animateToDefaultLocation()
            }
        }
    }

    private fun animateToDefaultLocation() {
        val defaultLocation = LatLng(-2.8126584, 108.6067792)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5f))
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    data class RecyclingLocation(val latLng: LatLng, val city: String, val plasticType: String)
}
