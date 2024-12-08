package com.dicoding.submission.storyapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.submission.storyapp.R
import com.dicoding.submission.storyapp.costumview.CustomEmail
import com.dicoding.submission.storyapp.costumview.CustomPassword
import com.dicoding.submission.storyapp.data.pref.DataStoreHelper
import com.dicoding.submission.storyapp.data.repository.AuthRepository
import com.dicoding.submission.storyapp.data.retrofit.ApiConfig
import com.dicoding.submission.storyapp.ui.register.RegisterActivity
import com.dicoding.submission.storyapp.ui.story.StoryActivity
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<CustomEmail>(R.id.emailEditText)
        val passwordEditText = findViewById<CustomPassword>(R.id.customPassword)
        val loginButton = findViewById<MaterialButton>(R.id.btn_login)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val txtGoToRegister = findViewById<TextView>(R.id.txtGoToRegister)

        // Buat ApiService tanpa token
        val apiService = ApiConfig.getApiService()
        val repository = AuthRepository(apiService)
        val factory = LoginViewModelFactory(repository)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        loginViewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(this, "Please enter valid email and password", Toast.LENGTH_SHORT).show()
            }
        }

        txtGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}



