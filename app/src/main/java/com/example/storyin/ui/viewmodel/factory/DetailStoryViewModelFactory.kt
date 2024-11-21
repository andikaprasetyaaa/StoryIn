package com.example.storyin.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyin.data.repository.StoryRepository
import com.example.storyin.ui.viewmodel.DetailStoryViewModel

class DetailStoryViewModelFactory(private val storyRepository: StoryRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailStoryViewModel::class.java)) {
            return DetailStoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}