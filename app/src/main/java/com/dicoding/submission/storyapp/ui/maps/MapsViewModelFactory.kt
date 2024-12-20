package com.dicoding.submission.storyapp.ui.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.submission.storyapp.data.repository.MapsRepository

class MapsViewModelFactory(private val mapsRepository: MapsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapsViewModel(mapsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
