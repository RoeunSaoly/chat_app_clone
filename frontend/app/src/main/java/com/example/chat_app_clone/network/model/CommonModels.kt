package com.example.chat_app_clone.network.model

import com.google.gson.annotations.SerializedName

data class GenericApiResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null
)
