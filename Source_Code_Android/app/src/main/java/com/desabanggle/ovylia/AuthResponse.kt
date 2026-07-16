package com.desabanggle.ovylia

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("username") val username: String?,
    @SerializedName("role") val role: String?
)