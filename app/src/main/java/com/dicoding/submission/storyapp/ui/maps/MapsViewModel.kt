package com.dicoding.submission.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.submission.storyapp.data.repository.MapsRepository
import com.dicoding.submission.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val mapsRepository: MapsRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            try {
                val response = mapsRepository.getStoriesWithLocation()
                if (response.error == false) {
                    // Filter out null items from the list
                    val filteredStories = response.listStory?.filterNotNull() ?: emptyList()
                    _stories.postValue(filteredStories)
                } else {
                    // Handle error (e.g., show a message to the user)
                }
            } catch (e: Exception) {
                // Handle exception (e.g., show a message to the user)
            }
        }
    }
}


