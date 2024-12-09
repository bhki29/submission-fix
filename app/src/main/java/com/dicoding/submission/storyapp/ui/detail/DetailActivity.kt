package com.dicoding.submission.storyapp.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.submission.storyapp.data.repository.StoryRepository
import com.dicoding.submission.storyapp.data.response.StoryDetail
import com.dicoding.submission.storyapp.databinding.ActivityDetailBinding
import com.dicoding.submission.storyapp.di.Injection
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var storyRepository: StoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra("STORY_ID") ?: return

        storyRepository = Injection.provideRepository(this)
        lifecycleScope.launch {
            val storyDetail = getStoryDetail(storyId)
            storyDetail?.let { bindData(it) }
        }
    }

    private suspend fun getStoryDetail(storyId: String): StoryDetail? {
        return try {
            val response = storyRepository.getStoryDetail(storyId)
            response.story
        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: "Failed to fetch story", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun bindData(story: StoryDetail) {
        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description
        Glide.with(this).load(story.photoUrl).into(binding.imgDetailPhoto)
    }
}

