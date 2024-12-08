package com.dicoding.submission.storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.submission.storyapp.data.repository.AuthRepository
import com.dicoding.submission.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _registerResponse = MutableLiveData<RegisterResponse?>()
    val registerResponse: LiveData<RegisterResponse?> = _registerResponse

    // Menambahkan LiveData untuk navigasi ke LoginActivity
    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.register(name, email, password)
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    // Jika berhasil, tampilkan pesan sukses
                    _registerResponse.value = it
                    _message.value = it.message ?: "Registration successful"

                    // Mengatur LiveData untuk navigasi ke LoginActivity
                    _navigateToLogin.value = true
                },
                onFailure = {
                    // Jika gagal, tampilkan pesan error
                    _message.value = it.localizedMessage ?: "An error occurred"
                }
            )
        }
    }
}


