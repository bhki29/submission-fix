package com.dicoding.submission.storyapp.ui.maps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.submission.storyapp.R
import com.dicoding.submission.storyapp.data.pref.DataStoreHelper
import com.dicoding.submission.storyapp.data.repository.MapsRepository
import com.dicoding.submission.storyapp.data.response.ListStoryItem
import com.dicoding.submission.storyapp.data.retrofit.ApiConfig
import com.dicoding.submission.storyapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.runBlocking

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = runBlocking { DataStoreHelper.getToken(applicationContext) }

        val apiService = ApiConfig.getApiService(token)
        val repository = MapsRepository(apiService)
        val factory = MapsViewModelFactory(repository)
        mapsViewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]

        // Observe live data to update map once data is fetched
        mapsViewModel.stories.observe(this) { stories ->
            addManyMarker(stories)  // Tambahkan marker ke peta
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // Fetch stories with location
        mapsViewModel.fetchStoriesWithLocation()
    }

    private fun addManyMarker(stories: List<ListStoryItem>) {

        val boundsBuilder = LatLngBounds.Builder()  // Inisialisasi boundsBuilder di sini

        stories.forEach { story ->
            val latLng = LatLng(story.lat as Double, story.lon as Double)  // Gunakan lat dan lon
            mMap.addMarker(MarkerOptions()
                .position(latLng)
                .title(story.name)
                .snippet(story.description))

            // Tambahkan setiap marker ke boundsBuilder
            boundsBuilder.include(latLng)
        }

        // Jika ada marker, buat LatLngBounds dan sesuaikan kamera
        if (stories.isNotEmpty()) {
            val bounds: LatLngBounds = boundsBuilder.build()
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
}

