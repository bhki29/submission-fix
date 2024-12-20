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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
        mapsViewModel.stories.observe(this, { stories ->
            addMarkersOnMap(stories)
        })

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

    private fun addMarkersOnMap(stories: List<ListStoryItem>) {
        stories.forEach { story ->
            val latLng = LatLng(story.lat ?: 0.0, story.lon ?: 0.0)
            mMap.addMarker(MarkerOptions().position(latLng).title(story.name))
        }
        if (stories.isNotEmpty()) {
            val firstStory = stories[0]
            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(firstStory.lat ?: 0.0, firstStory.lon ?: 0.0))
                .zoom(10f)
                .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }
}
