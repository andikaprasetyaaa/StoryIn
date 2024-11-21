package com.example.storyin.domain.result

import com.example.storyin.data.remote.response.StoryResponse

sealed class UploadResult {
    data class Success(val data: StoryResponse) : UploadResult()
    data class Error(val exception: Throwable) : UploadResult()
    data object Loading : UploadResult()
}