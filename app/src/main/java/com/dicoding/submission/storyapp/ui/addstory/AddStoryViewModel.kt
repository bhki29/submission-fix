package com.dicoding.submission.storyapp.ui.addstory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.submission.storyapp.data.repository.StoryRepository
import com.dicoding.submission.storyapp.data.response.BaseResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _uploadResult = MutableLiveData<BaseResponse>()
//    val uploadResult: LiveData<BaseResponse> = _uploadResult

    fun uploadStory(description: String, photo: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response = storyRepository.uploadStory(description, photo)
                _uploadResult.value = response
            } catch (e: Exception) {
                _uploadResult.value = BaseResponse(error = true, message = e.localizedMessage)
            }
        }
    }
}
