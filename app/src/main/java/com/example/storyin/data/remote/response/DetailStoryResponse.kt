package com.example.storyin.data.remote.response

import com.example.storyin.domain.model.StoryItem
import com.google.gson.annotations.SerializedName

data class DetailStoryResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("story")
    val story: StoryItem
)