package com.dicoding.submission.storyapp.data.repository

import com.dicoding.submission.storyapp.data.pref.UserPreference
import com.dicoding.submission.storyapp.data.response.StoryDetailResponse
import com.dicoding.submission.storyapp.data.response.StoryResponse
import com.dicoding.submission.storyapp.data.retrofit.ApiService

class StoryRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun getStories(): StoryResponse {
        return apiService.getStories() // Token ditambahkan oleh interceptor
    }

    suspend fun getStoryDetail(storyId: String): StoryDetailResponse {
        return apiService.getStoryDetail(storyId)
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
