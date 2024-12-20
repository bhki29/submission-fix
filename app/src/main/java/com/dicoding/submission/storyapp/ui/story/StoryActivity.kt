package com.dicoding.submission.storyapp.ui.story

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submission.storyapp.R
import com.dicoding.submission.storyapp.data.adapter.LoadingStateAdapter
import com.dicoding.submission.storyapp.data.adapter.StoryPagingAdapter
import com.dicoding.submission.storyapp.data.pref.DataStoreHelper
import com.dicoding.submission.storyapp.databinding.ActivityStoryBinding
import com.dicoding.submission.storyapp.di.Injection
import com.dicoding.submission.storyapp.ui.addstory.AddStoryActivity
import com.dicoding.submission.storyapp.ui.detail.DetailActivity
import com.dicoding.submission.storyapp.ui.intro.IntroActivity
import com.dicoding.submission.storyapp.ui.maps.MapsActivity
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {

    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyPagingDataAdapter: StoryPagingAdapter
    private lateinit var binding: ActivityStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView with ViewBinding
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the toolbar using ViewBinding
        val toolbar: Toolbar = findViewById(R.id.include_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Story App"

        // Initialize the adapter
        storyPagingDataAdapter = StoryPagingAdapter { storyId ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("STORY_ID", storyId)
            }
            startActivity(intent)
        }

        binding.recyclerView.adapter = storyPagingDataAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyPagingDataAdapter.retry()
            }
        )

        // Set up ViewModel using the repository
        val repository = Injection.provideRepository(applicationContext)
        val factory = StoryViewModelFactory(repository)
        storyViewModel = ViewModelProvider(this, factory).get(StoryViewModel::class.java)

        // Observe loading state
        storyViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe paging data and submit to the adapter
        storyViewModel.stories.observe(this) { pagingData ->
            lifecycleScope.launch {
                storyPagingDataAdapter.submitData(pagingData)
            }
        }

        // Observe error or message
        storyViewModel.message.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // Floating action button to add new story
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        // Logout button to clear session
        val logoutButton: ImageView = findViewById(R.id.btn_logout)
        logoutButton.setOnClickListener {
            lifecycleScope.launch {
                DataStoreHelper.clearLoginSession(applicationContext)
            }
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish()
        }

        //maps
        val mapsButton : ImageView = findViewById(R.id.btn_maps)
        mapsButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }
}




