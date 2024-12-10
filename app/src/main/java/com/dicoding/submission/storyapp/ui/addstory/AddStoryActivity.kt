package com.dicoding.submission.storyapp.ui.addstory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.submission.storyapp.data.pref.DataStoreHelper
import com.dicoding.submission.storyapp.data.retrofit.ApiConfig
import com.dicoding.submission.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.submission.storyapp.di.FileUtil
import com.dicoding.submission.storyapp.getImageUri
import com.dicoding.submission.storyapp.reduceFileImage
import com.dicoding.submission.storyapp.ui.story.StoryActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }
    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }
    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreview.setImageURI(it)
        }
    }
    private fun uploadImage() {
        val description = binding.description.text.toString()
        if (currentImageUri == null || description.isBlank()) {
            Toast.makeText(this, "Images and descriptions are required", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBarAddStory.visibility = View.VISIBLE
        binding.uploadButton.isEnabled = false // Disable tombol saat proses berlangsung

        lifecycleScope.launch {
            try {
                val token = DataStoreHelper.getToken(applicationContext) ?: ""
                val apiService = ApiConfig.getApiService(token)

                val contentResolver = applicationContext.contentResolver
                val originalFile = FileUtil.fromUri(contentResolver, currentImageUri!!)

                // compress file in reduceFileImage
                val compressedFile = originalFile.reduceFileImage()

                val requestImageFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    compressedFile.name,
                    requestImageFile
                )
                val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())

                val response = apiService.uploadStory(descriptionRequestBody, imageMultipart)

                if (!response.error) {
                    Toast.makeText(this@AddStoryActivity, "Story successfully uploaded", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddStoryActivity, StoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this@AddStoryActivity, response.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddStoryActivity, "Error occurred : ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {

                // Sembunyikan progress bar dan enable tombol setelah selesai
                binding.progressBarAddStory.visibility = View.GONE
                binding.uploadButton.isEnabled = true
            }
        }
    }
}