package com.example.storyin.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyin.data.remote.response.DetailStoryResponse
import com.example.storyin.data.repository.StoryRepository
import kotlinx.coroutines.launch

class DetailStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _detailStory = MutableLiveData<DetailStoryResponse>()
    val detailStory: LiveData<DetailStoryResponse> = _detailStory

    fun loadStoryDetail(storyId: String) {
        viewModelScope.launch {
            try {
                val result = storyRepository.getStoryDetail(storyId)
                _detailStory.value = result
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}