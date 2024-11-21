package com.example.storyin.data.repository

import com.example.storyin.data.preferences.UserPreference
import com.example.storyin.data.remote.api.ApiService
import com.example.storyin.data.remote.response.LoginResponse
import com.example.storyin.data.remote.response.RegisterResponse

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val response = apiService.login(email, password)
        userPreference.saveToken(response.login.token)
        return response
    }

    fun getToken() = userPreference.getToken()

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, userPreference)
            }.also { instance = it }
    }
}