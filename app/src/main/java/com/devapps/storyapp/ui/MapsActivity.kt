package com.devapps.storyapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.devapps.storyapp.R
import com.devapps.storyapp.data.Resource
import com.devapps.storyapp.data.model.Story
import com.devapps.storyapp.databinding.ActivityMapsBinding
import com.devapps.storyapp.viewmodel.MapsViewModel
import com.devapps.storyapp.viewmodel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val boundBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setMapFragment()
        setViewModel()
        setActionBar()

        requestLocationPermission()
    }

    private fun setMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setActionBar() {
        supportActionBar?.title = getString(R.string.maps_title)
    }

    private fun setViewModel() {
        viewModelFactory = ViewModelFactory.getInstance(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }


        setupStory()
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun setupStory() {
        lifecycleScope.launch {
            mapsViewModel.getStoriesLocation().observe(this@MapsActivity) { result ->
                when (result) {
                    is Resource.Error -> {
                        Toast.makeText(this@MapsActivity, "There is an error: ${result.message}", Toast.LENGTH_SHORT).show()
                        showLoad(false)
                    }
                    is Resource.Success -> {
                        showMarker(result.data?.listStory ?: emptyList())
                        showLoad(false)
                    }
                    is Resource.Loading -> {
                        showLoad(true)
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun showMarker(stories: List<Story>) {
        var hasValidMarkers = false

        stories.forEach { story ->
            val lat = story.lat
            val lon = story.lon

            val latLng = LatLng(lat, lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.description)
                    .snippet("Lat: ${latLng.latitude} Long: ${latLng.longitude}")
            )
            boundBuilder.include(latLng)
            hasValidMarkers = true
        }

        if (hasValidMarkers) {
            val bounds: LatLngBounds = boundBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

    private fun requestLocationPermission() {
        when {
            isLocationPermissionGranted() -> {
                setupStory()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setupStory()
            } else {
                Toast.makeText(
                    this,
                    "Location permission is required to show stories on the map.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

    private fun showLoad(isLoad: Boolean) {
        if (isLoad){
            binding.progressBar.visibility = View.VISIBLE
        }
        else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}
