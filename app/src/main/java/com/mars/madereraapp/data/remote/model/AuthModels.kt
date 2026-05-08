package com.mars.madereraapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val usuario: String,
    @SerializedName("password") val clave: String
)

data class LoginResponse(
    val mensaje: String?,
    val token: String,
    @SerializedName("usuario") val user: UserDto
)

data class UserDto(
    val id: Int,
    val nombre: String,
    @SerializedName("rol_id") val rolId: Int?
)
