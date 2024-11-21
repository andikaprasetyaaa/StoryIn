package com.example.storyin.di

import android.content.Context
import com.example.storyin.data.preferences.UserPreference
import com.example.storyin.data.remote.api.ApiConfig
import com.example.storyin.data.repository.AuthRepository
import com.example.storyin.data.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        val preferences = UserPreference.getInstance(context)
        return AuthRepository.getInstance(apiService, preferences)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val preferences = UserPreference.getInstance(context)
        return StoryRepository.getInstance(apiService, preferences)
    }

}