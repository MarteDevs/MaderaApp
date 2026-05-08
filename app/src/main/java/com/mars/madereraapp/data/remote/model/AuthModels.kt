package com.mars.madereraapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val usuario: String,
    @SerializedName("password") val clave: String
)

data class LoginResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val nombre: String,
    val usuario: String,
    val rol: String
)
