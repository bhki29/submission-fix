package com.dicoding.submission.storyapp.di

import android.content.Context
import com.dicoding.submission.storyapp.data.pref.UserPreference
import com.dicoding.submission.storyapp.data.pref.dataStore
import com.dicoding.submission.storyapp.data.repository.StoryRepository
import com.dicoding.submission.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(apiService, pref)
    }
}


