package com.example.storyin.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyin.data.repository.UploadStoryRepository
import com.example.storyin.ui.viewmodel.UploadStoryViewModel

class UploadStoryViewModelFactory(private val uploadStoryRepository: UploadStoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadStoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UploadStoryViewModel(uploadStoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}