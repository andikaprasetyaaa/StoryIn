package com.example.storyin.data.repository

import com.example.storyin.data.remote.api.ApiService
import com.example.storyin.data.remote.response.DetailStoryResponse
import com.example.storyin.data.preferences.UserPreference
import com.example.storyin.data.remote.response.StoryResponse
import kotlinx.coroutines.flow.first

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun getStoryDetail(storyId: String): DetailStoryResponse {
        val token = userPreference.getToken().first()
        return apiService.getStoryDetail("Bearer $token", storyId)
    }

    suspend fun getStories(
        page: Int? = null,
        size: Int? = null
    ): StoryResponse {
        val token = userPreference.getToken().first()
        return apiService.getStories("Bearer $token", page, size)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference).also { instance = it }
            }
    }
}