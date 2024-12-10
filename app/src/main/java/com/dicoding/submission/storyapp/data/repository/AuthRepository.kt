package com.dicoding.submission.storyapp.data.repository

import com.dicoding.submission.storyapp.data.response.ErrorResponse
import com.dicoding.submission.storyapp.data.response.LoginResponse
import com.dicoding.submission.storyapp.data.response.RegisterResponse
import com.dicoding.submission.storyapp.data.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.HttpException

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, password)
            Result.success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Result.failure(Exception(errorBody.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

