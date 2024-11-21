package com.example.storyin.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyin.domain.model.StoryItem
import com.example.storyin.data.repository.StoryRepository
import kotlinx.coroutines.launch

class StoryViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _stories = MutableLiveData<List<StoryItem>>()
    val stories: LiveData<List<StoryItem>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchStories(page: Int? = null, size: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = storyRepository.getStories(page, size)
                _stories.value = response.listStory
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
                _stories.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}