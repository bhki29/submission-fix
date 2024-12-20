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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = mapsRepository.getStoriesWithLocation()
                _isLoading.postValue(false)
                if (response.error == false) {
                    val filteredStories = response.listStory?.filterNotNull() ?: emptyList()
                    _stories.postValue(filteredStories)
                }
            } catch (e: Exception) {
                _isLoading.postValue(false)
            }
        }
    }
}


