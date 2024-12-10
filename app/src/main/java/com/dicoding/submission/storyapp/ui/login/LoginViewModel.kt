package com.dicoding.submission.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.submission.storyapp.data.repository.AuthRepository
import com.dicoding.submission.storyapp.data.response.ErrorResponse
import com.dicoding.submission.storyapp.data.response.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> = _loginResponse

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = authRepository.login(email, password)

                result.fold(
                    onSuccess = {
                        _loginResponse.value = it
                        _message.value = it.message
                    },
                    onFailure = {
                        _message.value = it.localizedMessage
                    }
                )
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                _message.value = errorResponse.message ?: "An error occurred"
            } catch (e: Exception) {
                _message.value = "Unexpected error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}


