package com.dicoding.submission.storyapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.submission.storyapp.R
import com.dicoding.submission.storyapp.costumview.CustomEmail
import com.dicoding.submission.storyapp.costumview.CustomPassword
import com.dicoding.submission.storyapp.data.repository.AuthRepository
import com.dicoding.submission.storyapp.data.retrofit.ApiConfig
import com.dicoding.submission.storyapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<CustomEmail>(R.id.emailEditText)
        val passwordEditText = findViewById<CustomPassword>(R.id.customPassword)
        val registerButton = findViewById<Button>(R.id.btn_register)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val txtGoToLogin = findViewById<TextView>(R.id.txtGoToLogin)

        // Initialize ViewModel
        val apiService = ApiConfig.getApiService()
        val repository = AuthRepository(apiService)
        val factory = RegisterViewModelFactory(repository)
        registerViewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        // Observe loading state
        registerViewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe messages (success or error)
        registerViewModel.message.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // Observe navigasi ke LoginActivity
        registerViewModel.navigateToLogin.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()  // Menutup RegisterActivity
            }
        }

        // Handle register button click
        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerViewModel.register(name, email, password)
            } else {
                Toast.makeText(this, "Please fill in all available fields", Toast.LENGTH_SHORT).show()
            }
        }

        // TextView untuk pindah ke LoginActivity
        txtGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}


