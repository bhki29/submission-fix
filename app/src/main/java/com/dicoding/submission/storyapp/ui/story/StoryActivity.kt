package com.dicoding.submission.storyapp.ui.story

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.submission.storyapp.R
import com.dicoding.submission.storyapp.data.adapter.StoryAdapter
import com.dicoding.submission.storyapp.data.pref.DataStoreHelper
import com.dicoding.submission.storyapp.di.Injection
import com.dicoding.submission.storyapp.ui.addstory.AddStoryActivity
import com.dicoding.submission.storyapp.ui.detail.DetailActivity
import com.dicoding.submission.storyapp.ui.intro.IntroActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {

    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val toolbar: Toolbar = findViewById(R.id.include_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Story App"

        storyAdapter = StoryAdapter(emptyList()) { storyId ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("STORY_ID", storyId) // Pass the story ID
            }
            startActivity(intent)
        }
        recyclerView.adapter = storyAdapter

        val repository = Injection.provideRepository(applicationContext)
        val factory = StoryViewModelFactory(repository)
        storyViewModel = ViewModelProvider(this, factory).get(StoryViewModel::class.java)

        storyViewModel.isLoading.observe(this) { isLoading ->
            findViewById<ProgressBar>(R.id.progressBar).visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        storyViewModel.stories.observe(this) { stories ->
            storyAdapter = StoryAdapter(stories) { storyId ->
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("STORY_ID", storyId)
                }
                startActivity(intent)
            }
            recyclerView.adapter = storyAdapter
        }

        storyViewModel.message.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        storyViewModel.getStories()

        val fabAddStory: FloatingActionButton = findViewById(R.id.fab_add)
        fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        val logoutButton: ImageView = findViewById(R.id.btn_logout)
        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                DataStoreHelper.clearLoginSession(applicationContext)
            }
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

