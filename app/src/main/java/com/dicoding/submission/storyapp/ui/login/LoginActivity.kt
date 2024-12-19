package com.dicoding.submission.storyapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.submission.storyapp.R
import com.dicoding.submission.storyapp.data.pref.DataStoreHelper
import com.dicoding.submission.storyapp.data.repository.AuthRepository
import com.dicoding.submission.storyapp.data.retrofit.ApiConfig
import com.dicoding.submission.storyapp.databinding.ActivityLoginBinding
import com.dicoding.submission.storyapp.ui.register.RegisterActivity
import com.dicoding.submission.storyapp.ui.story.StoryActivity
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenCreated {
            DataStoreHelper.isLoggedIn(applicationContext).collect { isLoggedIn ->
                if (isLoggedIn) {
                    val intent = Intent(this@LoginActivity, StoryActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@collect
                }
            }
        }

        val apiService = ApiConfig.getApiService()
        val repository = AuthRepository(apiService)
        val factory = LoginViewModelFactory(repository)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        loginViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        loginViewModel.message.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        loginViewModel.loginResponse.observe(this) { response ->
            if (response != null && !response.error!!) {
                lifecycleScope.launch {
                    response.loginResult?.token?.let { token ->
                        DataStoreHelper.saveLoginSession(applicationContext, token)
                    }
                    val intent = Intent(this@LoginActivity, StoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.customPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(this,
                    getString(R.string.please_enter_valid_email_and_password), Toast.LENGTH_SHORT).show()
            }
        }

        binding.txtGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}




