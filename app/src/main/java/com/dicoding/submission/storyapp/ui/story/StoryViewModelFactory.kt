package com.dicoding.submission.storyapp.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.submission.storyapp.data.repository.StoryRepository
import com.dicoding.submission.storyapp.ui.addstory.AddStoryViewModel

class StoryViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> StoryViewModel(storyRepository) as T
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> AddStoryViewModel(storyRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
