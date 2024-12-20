package com.dicoding.submission.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.submission.storyapp.data.paging.StoryPagingSource
import com.dicoding.submission.storyapp.data.pref.UserPreference
import com.dicoding.submission.storyapp.data.response.BaseResponse
import com.dicoding.submission.storyapp.data.response.ListStoryItem
import com.dicoding.submission.storyapp.data.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40
            ),
            pagingSourceFactory = { getStoryPagingSource() }
        ).liveData
    }

    suspend fun uploadStory(description: String, photo: MultipartBody.Part): BaseResponse {
        return apiService.uploadStory(description.toRequestBody("text/plain".toMediaType()), photo)
    }

    private fun getStoryPagingSource(): StoryPagingSource {
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
