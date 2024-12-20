package com.dicoding.submission.storyapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.submission.storyapp.R
import com.dicoding.submission.storyapp.data.repository.AuthRepository
import com.dicoding.submission.storyapp.data.retrofit.ApiConfig
import com.dicoding.submission.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.submission.storyapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfig.getApiService()
        val repository = AuthRepository(apiService)
        val factory = RegisterViewModelFactory(repository)
        registerViewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        registerViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        registerViewModel.message.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        registerViewModel.navigateToLogin.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.customPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerViewModel.register(name, email, password)
            } else {
                Toast.makeText(this,
                    getString(R.string.please_fill_in_all_available_fields), Toast.LENGTH_SHORT).show()
            }
        }

        binding.txtGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}



