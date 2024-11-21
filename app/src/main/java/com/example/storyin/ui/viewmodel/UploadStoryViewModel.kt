package com.example.storyin.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyin.data.repository.UploadStoryRepository
import com.example.storyin.domain.result.UploadResult
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadStoryViewModel(private val uploadStoryRepository: UploadStoryRepository) : ViewModel() {

    val uploadResult: LiveData<UploadResult> = uploadStoryRepository.uploadResult

    private val _selectedImage = MutableLiveData<Bitmap?>(null)
    val selectedImage: LiveData<Bitmap?> = _selectedImage

    fun setSelectedImage(bitmap: Bitmap) {
        _selectedImage.value = bitmap
    }

    fun clearSelectedImage() {
        _selectedImage.value = null
    }

    fun uploadStory(token: String, description: RequestBody, file: MultipartBody.Part) {
        viewModelScope.launch {
            uploadStoryRepository.uploadStory(token, description, file)
        }
    }
}