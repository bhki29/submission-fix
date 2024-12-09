package com.dicoding.submission.storyapp.ui.detail

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.submission.storyapp.data.pref.DataStoreHelper
import com.dicoding.submission.storyapp.data.repository.StoryRepository
import com.dicoding.submission.storyapp.data.retrofit.ApiConfig
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

        // Ambil ID dari Intent
        val storyId = intent.getStringExtra("STORY_ID") ?: return

        // Inisialisasi Repository
        storyRepository = Injection.provideRepository(this)

        // Ambil Detail Story
        fetchStoryDetail(storyId)
    }

    private fun fetchStoryDetail(storyId: String) {
        lifecycleScope.launch {
            try {
                val token = DataStoreHelper.getToken(applicationContext) ?: ""
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getStoryDetail(storyId)

                if (response.error == false) {
                    val story = response.story
                    binding.tvDetailName.text = story.name
                    binding.tvDetailDescription.text = story.description
                    Glide.with(this@DetailActivity)
                        .load(story.photoUrl)
                        .into(binding.imgDetailPhoto)
                } else {
                    Toast.makeText(
                        this@DetailActivity,
                        response.message ?: "Error fetching story",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

