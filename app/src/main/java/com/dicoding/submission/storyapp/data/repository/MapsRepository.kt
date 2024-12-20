package com.dicoding.submission.storyapp.data.repository

import com.dicoding.submission.storyapp.data.response.StoryResponse
import com.dicoding.submission.storyapp.data.retrofit.ApiService

class MapsRepository(private val apiService: ApiService) {

    suspend fun getStoriesWithLocation(): StoryResponse {
        return apiService.getStoriesWithLocation()
    }
}
