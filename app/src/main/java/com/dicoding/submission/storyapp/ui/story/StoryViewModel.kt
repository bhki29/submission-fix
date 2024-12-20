package com.dicoding.submission.storyapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.dicoding.submission.storyapp.data.repository.StoryRepository
import com.dicoding.submission.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            initialLoadSize = 40
        ),
        pagingSourceFactory = { storyRepository.getStoryPagingSource() }
    ).flow.cachedIn(viewModelScope)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun getStories() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = storyRepository.getStories()
                _isLoading.value = false
                if (response.error == true) {
                    _message.value = response.message ?: "Error"
                } else {
                    _stories.value = response.listStory?.filterNotNull() ?: emptyList()
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _message.value = e.localizedMessage ?: "An error occurred"
            }
        }
    }
}
