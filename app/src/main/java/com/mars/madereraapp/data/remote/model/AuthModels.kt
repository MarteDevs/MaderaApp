package com.mars.madereraapp.data.remote.model

data class LoginRequest(
    val usuario: String,
    val clave: String
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
