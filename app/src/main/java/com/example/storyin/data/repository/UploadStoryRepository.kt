package com.example.storyin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.storyin.data.remote.api.ApiService
import com.example.storyin.domain.result.UploadResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException

class UploadStoryRepository private constructor(
    private val apiService: ApiService
) {
    private val _uploadResult = MutableLiveData<UploadResult>()
    val uploadResult: LiveData<UploadResult> = _uploadResult

    suspend fun uploadStory(token: String, description: RequestBody, file: MultipartBody.Part) {
        _uploadResult.postValue(UploadResult.Loading)
        try {
            val response = apiService.uploadStory(token, description, file)
            _uploadResult.postValue(UploadResult.Success(response))
        } catch (e: HttpException) {
            _uploadResult.postValue(UploadResult.Error(e))
        } catch (e: IOException) {
            _uploadResult.postValue(UploadResult.Error(e))
        } catch (e: Exception) {
            _uploadResult.postValue(UploadResult.Error(e))
        }
    }

    companion object {
        @Volatile
        private var instance: UploadStoryRepository? = null

        fun getInstance(apiService: ApiService): UploadStoryRepository =
            instance ?: synchronized(this) {
                instance ?: UploadStoryRepository(apiService).also { instance = it }
            }
    }
}