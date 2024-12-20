package com.dicoding.submission.storyapp.data.repository

import com.dicoding.submission.storyapp.data.paging.StoryPagingSource
import com.dicoding.submission.storyapp.data.pref.UserPreference
import com.dicoding.submission.storyapp.data.response.BaseResponse
import com.dicoding.submission.storyapp.data.response.StoryResponse
import com.dicoding.submission.storyapp.data.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun getStories(): StoryResponse {
        return apiService.getStories()
    }

    suspend fun uploadStory(description: String, photo: MultipartBody.Part): BaseResponse {
        return apiService.uploadStory(description.toRequestBody("text/plain".toMediaType()), photo)
    }

    fun getStoryPagingSource(): StoryPagingSource {
        return StoryPagingSource(apiService)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference).also { instance = it }
            }
    }
}
