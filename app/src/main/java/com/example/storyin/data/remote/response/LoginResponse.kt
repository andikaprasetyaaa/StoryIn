package com.example.storyin.data.remote.response

import com.example.storyin.domain.model.Login
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("loginResult")
    val login: Login
)